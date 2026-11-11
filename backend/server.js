/**
 * AI Video Studio Pro - Backend Server (Node.js & Express)
 * Provides REST APIs for:
 * - Multilingual AI Script Writing (Hindi & English)
 * - FFmpeg Video Rendering & Subtitle Burn-In
 * - Asynchronous Video Generation Job Queue
 * - Project & Scene Synchronization
 * - Admin Metrics & Audit Logs
 */

const express = require('express');
const cors = require('cors');
const path = require('path');
const fs = require('fs');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5000;

// Middleware
app.use(cors());
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ extended: true, limit: '50mb' }));

// Ensure upload & output directories exist
const uploadDir = path.join(__dirname, 'public', 'uploads');
const outputDir = path.join(__dirname, 'public', 'outputs');
if (!fs.existsSync(uploadDir)) fs.mkdirSync(uploadDir, { recursive: true });
if (!fs.existsSync(outputDir)) fs.mkdirSync(outputDir, { recursive: true });

app.use('/media', express.static(path.join(__dirname, 'public')));

// Mount API Routes
const authRouter = require('./routes/auth');
const aiRouter = require('./routes/ai');
const renderRouter = require('./routes/render');
const adminRouter = require('./routes/admin');
const updatesRouter = require('./routes/updates');

app.use('/api/auth', authRouter);
app.use('/api/ai', aiRouter);
app.use('/api/render', renderRouter);
app.use('/api/admin', adminRouter);
app.use('/api/updates', updatesRouter);

// Health Check Endpoint
app.get('/api/health', (req, res) => {
  let ffmpegAvailable = false;
  try {
    const { execSync } = require('child_process');
    execSync('ffmpeg -version', { stdio: 'ignore' });
    ffmpegAvailable = true;
  } catch (err) {
    ffmpegAvailable = false;
  }

  res.json({
    status: 'online',
    version: '2.4.0',
    serverTime: new Date().toISOString(),
    activeProviders: ['Gemini', 'OpenAI', 'Runway', 'Local-FFmpeg'],
    ffmpegAvailable: ffmpegAvailable
  });
});

// Error handling middleware
app.use((err, req, res, next) => {
  console.error('[SERVER ERROR]', err);
  res.status(500).json({
    success: false,
    message: err.message || 'Internal Server Error',
    timestamp: Date.now()
  });
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`===============================================`);
  console.log(`🎬 AI Video Studio Pro Server running on port ${PORT}`);
  console.log(`📡 Health Check: http://localhost:${PORT}/api/health`);
  console.log(`===============================================`);
});

module.exports = app;
