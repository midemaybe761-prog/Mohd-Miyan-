const express = require('express');
const router = express.Router();
const { v4: uuidv4 } = require('uuid');

// In-memory active video generation jobs queue
const jobQueue = new Map();

// POST /api/ai/script
// Generates structured multi-scene scripts with hooks in English or Hindi
router.post('/script', async (req, res) => {
  const { prompt, language = 'English', genre = 'Educational', durationSeconds = 60, visualStyle = 'Cinematic' } = req.body;

  if (!prompt) {
    return res.status(400).json({ success: false, message: 'Prompt is required' });
  }

  const isHindi = language.toLowerCase().includes('hindi') || /[\u0900-\u097F]/.test(prompt);
  const sceneCount = durationSeconds <= 30 ? 3 : durationSeconds <= 60 ? 4 : 6;
  const durationPerScene = Math.max(4, Math.floor(durationSeconds / sceneCount));

  const hook = isHindi
    ? `रुकिए! ${prompt.slice(0, 30)} का यह सबसे बड़ा रहस्य आपको हैरान कर देगा!`
    : `Stop scrolling! Here is the shocking truth about ${prompt.slice(0, 30)} that everyone is talking about.`;

  const scenes = [];
  for (let i = 1; i <= sceneCount; i++) {
    const title = isHindi ? `दृश्य ${i}: विश्लेषण` : `Scene ${i}: Core Breakdown`;
    const narration = isHindi
      ? `नमस्ते दोस्तों, आज के इस भाग में हम ${prompt} के महत्वपूर्ण पहलू पर विस्तार से चर्चा कर रहे हैं।`
      : `In this segment, we examine how ${prompt} is revolutionizing modern standards across the board.`;
    const visual = `${visualStyle} style footage representing ${prompt}, ultra high-res cinematic lighting, 8k resolution`;
    const camera = i % 2 === 0 ? 'Wide cinematic panning shot' : 'Dramatic close-up with shallow depth of field';
    const overlay = isHindi ? `प्रमुख बिंदु #${i}` : `KEY POINT #${i}`;

    scenes.push({
      sceneOrder: i,
      durationSeconds: durationPerScene,
      title: title,
      visualPrompt: visual,
      narration: narration,
      cameraAngle: camera,
      textOverlay: overlay
    });
  }

  const fullScript = scenes.map(s => `${s.title}\nNarration: ${s.narration}\nVisual: ${s.visualPrompt}`).join('\n\n');

  res.json({
    success: true,
    hook: hook,
    suggestedTitle: isHindi ? `${prompt.slice(0, 25)}: संपूर्ण विश्लेषण` : `${prompt.slice(0, 25)} | Master Breakdown`,
    fullScript: fullScript,
    scenes: scenes
  });
});

// POST /api/ai/video-job
// Submits asynchronous video generation job
router.post('/video-job', (req, res) => {
  const { projectId, title, provider = 'Runway-Gen3', canvasRatio, quality, scenes } = req.body;

  const jobId = 'job_' + uuidv4().slice(0, 8);
  const newJob = {
    jobId: jobId,
    projectId: projectId,
    title: title || 'Untitled AI Video',
    provider: provider,
    status: 'Processing',
    progress: 10,
    startedAt: Date.now(),
    durationSeconds: (scenes || []).reduce((acc, s) => acc + (s.durationSeconds || 5), 0),
    downloadUrl: null,
    errorMessage: null
  };

  jobQueue.set(jobId, newJob);

  // Simulate background AI generation progress
  let currentProgress = 10;
  const interval = setInterval(() => {
    currentProgress += 20;
    const j = jobQueue.get(jobId);
    if (!j) {
      clearInterval(interval);
      return;
    }

    if (currentProgress >= 100) {
      j.progress = 100;
      j.status = 'Completed';
      j.downloadUrl = `/media/outputs/video_${jobId}.mp4`;
      clearInterval(interval);
    } else {
      j.progress = currentProgress;
    }
  }, 2000);

  res.status(202).json({
    success: true,
    jobId: jobId,
    status: 'Processing',
    progress: 10
  });
});

// GET /api/ai/video-job/:jobId
// Polls asynchronous video job status
router.get('/video-job/:jobId', (req, res) => {
  const { jobId } = req.params;
  const job = jobQueue.get(jobId);

  if (!job) {
    return res.status(404).json({ success: false, message: 'Job not found' });
  }

  res.json({
    success: true,
    jobId: job.jobId,
    status: job.status,
    progress: job.progress,
    downloadUrl: job.downloadUrl,
    errorMessage: job.errorMessage
  });
});

module.exports = router;
