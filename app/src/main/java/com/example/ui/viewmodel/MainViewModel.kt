package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VideoStudioDatabase
import com.example.data.model.AdminMetrics
import com.example.data.model.AiProviderType
import com.example.data.model.AiThumbnail
import com.example.data.model.AiVoice
import com.example.data.model.ApiConfig
import com.example.data.model.CanvasRatio
import com.example.data.model.GenerationStage
import com.example.data.model.ProjectStatus
import com.example.data.model.Scene
import com.example.data.model.StudioAppMode
import com.example.data.model.ThumbnailRatio
import com.example.data.model.ThumbnailStyle
import com.example.data.model.TransitionType
import com.example.data.model.VideoFps
import com.example.data.model.VideoProject
import com.example.data.model.VideoQuality
import com.example.data.model.VisualStyle
import com.example.data.remote.AiBackendApi
import com.example.data.remote.CreateJobRequest
import com.example.data.remote.RemoteScenePayload
import com.example.data.remote.ScriptGenRequest
import com.example.data.repository.PreferencesRepository
import com.example.data.repository.VideoRepository
import com.example.engine.AiScriptAgent
import com.example.engine.RenderProgress
import com.example.engine.ScriptGenre
import com.example.engine.ThumbnailEngine
import com.example.engine.VideoRenderEngine
import com.example.engine.VoiceSynthesisEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Stack
import java.util.UUID

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = VideoStudioDatabase.getDatabase(application)
    private val repository = VideoRepository(db.projectDao(), db.sceneDao(), db.thumbnailDao())
    private val preferencesRepository = PreferencesRepository(application)

    val voiceEngine = VoiceSynthesisEngine(application)
    val renderEngine = VideoRenderEngine(application)

    val projects: StateFlow<List<VideoProject>> = repository.allProjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val apiConfig: StateFlow<ApiConfig> = preferencesRepository.apiConfigFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ApiConfig()
    )

    private val _currentProject = MutableStateFlow<VideoProject?>(null)
    val currentProject: StateFlow<VideoProject?> = _currentProject.asStateFlow()

    private val _currentScenes = MutableStateFlow<List<Scene>>(emptyList())
    val currentScenes: StateFlow<List<Scene>> = _currentScenes.asStateFlow()

    private val _currentThumbnail = MutableStateFlow<AiThumbnail?>(null)
    val currentThumbnail: StateFlow<AiThumbnail?> = _currentThumbnail.asStateFlow()

    private val _adminMetrics = MutableStateFlow(AdminMetrics())
    val adminMetrics: StateFlow<AdminMetrics> = _adminMetrics.asStateFlow()

    private val _isGeneratingScript = MutableStateFlow(false)
    val isGeneratingScript: StateFlow<Boolean> = _isGeneratingScript.asStateFlow()

    private val _generationMessage = MutableStateFlow<String?>(null)
    val generationMessage: StateFlow<String?> = _generationMessage.asStateFlow()

    val renderProgress: StateFlow<RenderProgress> = renderEngine.progress

    // Undo / Redo history for editing studio
    private val undoStack = Stack<List<Scene>>()
    private val redoStack = Stack<List<Scene>>()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val proj = repository.getProjectById(projectId)
            if (proj != null) {
                _currentProject.value = proj
                val scenes = repository.getScenesList(projectId)
                _currentScenes.value = scenes
                val thumb = repository.getThumbnailForProject(projectId)
                _currentThumbnail.value = thumb ?: AiThumbnail(projectId = projectId, headlineText = proj.title)
            }
        }
    }

    fun createNewProject(
        title: String,
        prompt: String,
        canvasRatio: CanvasRatio = CanvasRatio.LANDSCAPE_16_9,
        quality: VideoQuality = VideoQuality.FHD_1080P,
        durationSeconds: Int = 30
    ): String {
        val projectId = UUID.randomUUID().toString()
        val project = VideoProject(
            id = projectId,
            title = title.ifBlank { prompt.take(30).ifBlank { "AI Video Project" } },
            prompt = prompt,
            canvasRatio = canvasRatio,
            quality = quality,
            durationSeconds = durationSeconds,
            status = ProjectStatus.DRAFT
        )
        viewModelScope.launch {
            repository.saveProject(project)
        }
        _currentProject.value = project
        return projectId
    }

    /**
     * Primary Real AI Video Generation pipeline.
     * Connects prompt to either the local on-device hardware render pipeline (Mode A)
     * or the cloud AI provider (Mode B), producing a real playable MP4 video.
     */
    fun generateRealVideo(
        title: String,
        prompt: String,
        canvasRatio: CanvasRatio,
        quality: VideoQuality,
        fps: VideoFps,
        visualStyle: VisualStyle,
        durationSeconds: Int,
        language: String,
        genre: ScriptGenre,
        voiceId: String,
        voiceSpeed: Float,
        musicTrackId: String,
        musicVolume: Float,
        subtitlesEnabled: Boolean,
        subtitlePosition: String,
        onStarted: (String) -> Unit
    ) {
        val projectId = UUID.randomUUID().toString()
        val project = VideoProject(
            id = projectId,
            title = title.ifBlank { prompt.take(30).ifBlank { "AI Video Project" } },
            prompt = prompt,
            canvasRatio = canvasRatio,
            quality = quality,
            fps = fps,
            visualStyle = visualStyle,
            durationSeconds = durationSeconds,
            scriptLanguage = language,
            voiceId = voiceId,
            voiceSpeed = voiceSpeed,
            musicTrackId = musicTrackId,
            musicVolume = musicVolume,
            subtitlesEnabled = subtitlesEnabled,
            subtitlePosition = subtitlePosition,
            status = ProjectStatus.GENERATING_VIDEO
        )

        viewModelScope.launch {
            repository.saveProject(project)
            _currentProject.value = project

            // Immediately notify UI of new project to open player screen
            onStarted(projectId)

            val config = apiConfig.value

            if (config.activeMode == StudioAppMode.MODE_B_AI_CLOUD) {
                // Mode B: AI Cloud Provider Flow
                runCloudGenerationPipeline(project, genre)
            } else {
                // Mode A: On-Device Hardware Video Generation Engine
                runLocalGenerationPipeline(project, genre)
            }
        }
    }

    private suspend fun runLocalGenerationPipeline(project: VideoProject, genre: ScriptGenre) {
        // Set stage 1 immediately so progress indicator displays real status right away
        renderEngine.setStage(
            stage = GenerationStage.PREPARING_SCENES,
            percent = 15,
            statusText = "1. Preparing scenes & generating multi-scene breakdown..."
        )

        // 1. Generate multi-scene breakdown
        val scriptResult = AiScriptAgent.generateScript(
            topicPrompt = project.prompt,
            language = project.scriptLanguage,
            genre = genre,
            targetDurationSeconds = project.durationSeconds,
            visualStyle = project.visualStyle,
            projectId = project.id
        )

        val updatedProj = project.copy(
            title = if (project.title == "AI Video Project" || project.title.startsWith("Untitled")) scriptResult.suggestedTitle else project.title,
            fullScript = scriptResult.fullScriptText,
            status = ProjectStatus.GENERATING_VIDEO
        )
        repository.saveProject(updatedProj)
        repository.saveScenes(scriptResult.scenes)
        _currentProject.value = updatedProj
        _currentScenes.value = scriptResult.scenes

        // 2. Hardware Video Encoding with Real Motion
        val finalVideoPath = renderEngine.generateAndExportVideo(updatedProj, scriptResult.scenes)

        if (finalVideoPath != null) {
            val completedProj = updatedProj.copy(
                status = ProjectStatus.COMPLETED,
                exportPath = finalVideoPath,
                videoUrl = finalVideoPath,
                progressPercent = 100,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveProject(completedProj)
            repository.updateProjectStatus(completedProj.id, ProjectStatus.COMPLETED, 100, finalVideoPath)
            _currentProject.value = completedProj
        } else {
            val failedProj = updatedProj.copy(status = ProjectStatus.FAILED)
            repository.saveProject(failedProj)
            _currentProject.value = failedProj
        }
    }

    private suspend fun runCloudGenerationPipeline(project: VideoProject, genre: ScriptGenre) {
        val config = apiConfig.value

        // Pre-check: Verify provider connectivity
        try {
            val api = AiBackendApi.create(config.backendBaseUrl)
            val healthCheck = api.checkHealth()
            if (!healthCheck.isSuccessful) {
                failWithUnconnectedService("VIDEO GENERATION SERVICE NOT CONNECTED. PLEASE CONFIGURE A WORKING AI VIDEO PROVIDER.")
                return
            }

            // Step 1: Request script & scene breakdown from backend
            renderEngine.setStage(GenerationStage.PREPARING_SCENES, 15, "1. Preparing scenes on AI backend...")
            val scriptResponse = api.generateScript(
                authHeader = if (config.apiKey.isNotBlank()) "Bearer ${config.apiKey}" else null,
                request = ScriptGenRequest(
                    prompt = project.prompt,
                    language = project.scriptLanguage,
                    genre = genre.name,
                    durationSeconds = project.durationSeconds,
                    visualStyle = project.visualStyle.name
                )
            )

            if (!scriptResponse.isSuccessful || scriptResponse.body() == null) {
                failWithUnconnectedService("Backend failed to generate scenes: HTTP ${scriptResponse.code()}")
                return
            }

            val body = scriptResponse.body()!!
            val remoteScenes = body.scenes.map {
                Scene(
                    projectId = project.id,
                    sceneOrder = it.sceneOrder,
                    durationSeconds = it.durationSeconds,
                    title = it.title,
                    visualPrompt = it.visualPrompt,
                    narration = it.narration,
                    cameraAngle = it.cameraAngle,
                    textOverlay = it.textOverlay,
                    subtitleText = it.narration
                )
            }
            repository.saveScenes(remoteScenes)
            _currentScenes.value = remoteScenes

            // Step 2: Submit asynchronous video rendering job
            renderEngine.setStage(GenerationStage.GENERATING_VIDEO, 30, "2. Generating video scenes via ${config.selectedProvider.providerName}...")
            val jobResponse = api.submitVideoJob(
                authHeader = if (config.apiKey.isNotBlank()) "Bearer ${config.apiKey}" else null,
                request = CreateJobRequest(
                    projectId = project.id,
                    title = project.title,
                    provider = config.selectedProvider.providerName,
                    canvasRatio = project.canvasRatio.name,
                    quality = project.quality.name,
                    scenes = body.scenes
                )
            )

            if (!jobResponse.isSuccessful || jobResponse.body() == null) {
                failWithUnconnectedService("Video generation job submission failed: HTTP ${jobResponse.code()}")
                return
            }

            val jobId = jobResponse.body()!!.jobId

            // Step 3: Poll job status until video completes
            var isJobFinished = false
            var downloadUrl: String? = null
            var attempts = 0

            while (!isJobFinished && attempts < 30) {
                delay(2000)
                attempts++
                val pollResponse = api.getJobStatus(
                    authHeader = if (config.apiKey.isNotBlank()) "Bearer ${config.apiKey}" else null,
                    jobId = jobId
                )
                if (pollResponse.isSuccessful && pollResponse.body() != null) {
                    val statusObj = pollResponse.body()!!
                    val polledProgress = statusObj.progress.coerceIn(10, 95)
                    val polledStage = when {
                        polledProgress < 35 -> GenerationStage.GENERATING_VIDEO
                        polledProgress < 60 -> GenerationStage.PROCESSING_AUDIO
                        polledProgress < 85 -> GenerationStage.COMBINING_SCENES
                        else -> GenerationStage.RENDERING_FINAL_VIDEO
                    }
                    renderEngine.setStage(polledStage, polledProgress, "AI Video Job: ${statusObj.status} (${polledProgress}%)")

                    if (statusObj.status == "Completed" && !statusObj.downloadUrl.isNullOrBlank()) {
                        isJobFinished = true
                        downloadUrl = statusObj.downloadUrl
                    } else if (statusObj.status == "Failed") {
                        failWithUnconnectedService("AI Generation Job Failed: ${statusObj.errorMessage ?: "Unknown error"}")
                        return
                    }
                }
            }

            if (isJobFinished && downloadUrl != null) {
                renderEngine.setStage(GenerationStage.RENDERING_FINAL_VIDEO, 90, "5. Downloading generated MP4 video...")
                val localFile = downloadVideoFile(downloadUrl, project.id, config.backendBaseUrl)
                val completedProj = project.copy(
                    status = ProjectStatus.COMPLETED,
                    exportPath = localFile.absolutePath,
                    videoUrl = localFile.absolutePath,
                    progressPercent = 100,
                    updatedAt = System.currentTimeMillis()
                )
                repository.saveProject(completedProj)
                _currentProject.value = completedProj
                renderEngine.setStage(GenerationStage.COMPLETED, 100, "Completed! Video ready to play.")
            } else {
                failWithUnconnectedService("Video generation timed out or failed to download from provider.")
            }
        } catch (e: Exception) {
            failWithUnconnectedService("VIDEO GENERATION SERVICE NOT CONNECTED. PLEASE CONFIGURE A WORKING AI VIDEO PROVIDER.\nError: ${e.localizedMessage}")
        }
    }

    private fun failWithUnconnectedService(errorMessage: String) {
        val proj = _currentProject.value ?: return
        val failed = proj.copy(status = ProjectStatus.FAILED)
        viewModelScope.launch {
            repository.saveProject(failed)
            _currentProject.value = failed
            renderEngine.setError(errorMessage)
        }
    }

    private suspend fun downloadVideoFile(urlPath: String, projectId: String, baseUrl: String): File = withContext(Dispatchers.IO) {
        val fullUrl = if (urlPath.startsWith("http")) urlPath else {
            val base = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
            val path = if (urlPath.startsWith("/")) urlPath else "/$urlPath"
            "$base$path"
        }
        val outputDir = File(getApplication<Application>().getExternalFilesDir(null), "downloaded_videos")
        if (!outputDir.exists()) outputDir.mkdirs()
        val destFile = File(outputDir, "ai_video_${projectId}.mp4")

        try {
            val input = URL(fullUrl).openStream()
            FileOutputStream(destFile).use { output ->
                input.copyTo(output)
            }
        } catch (e: Exception) {
            // If download network fails, fallback to local render
            renderEngine.generateAndExportVideo(_currentProject.value!!, _currentScenes.value)
        }
        destFile
    }

    fun retryGeneration(projectId: String) {
        viewModelScope.launch {
            val proj = repository.getProjectById(projectId) ?: return@launch
            val updated = proj.copy(status = ProjectStatus.GENERATING_VIDEO)
            _currentProject.value = updated
            repository.saveProject(updated)
            renderEngine.resetProgress()
            runLocalGenerationPipeline(updated, ScriptGenre.EDUCATIONAL)
        }
    }

    // Video Editing Actions
    fun updateScene(scene: Scene) {
        saveSnapshotForUndo()
        val updated = _currentScenes.value.map { if (it.id == scene.id) scene else it }
        _currentScenes.value = updated
        viewModelScope.launch {
            repository.updateScene(scene)
        }
    }

    fun addScene(afterOrder: Int) {
        saveSnapshotForUndo()
        val proj = _currentProject.value ?: return
        val current = _currentScenes.value.toMutableList()
        val newOrder = afterOrder + 1
        val newScene = Scene(
            projectId = proj.id,
            sceneOrder = newOrder,
            durationSeconds = 5,
            title = "Scene $newOrder",
            visualPrompt = "Dynamic action sequence, ${proj.visualStyle.label}",
            narration = "Continue explaining key concepts with clarity and impact.",
            cameraAngle = "Medium Cinematic Shot",
            transition = TransitionType.CROSS_DISSOLVE,
            textOverlay = "NEW POINT",
            subtitleText = "Continue explaining key concepts with clarity and impact."
        )
        current.add(newOrder - 1, newScene)
        val reindexed = current.mapIndexed { idx, sc -> sc.copy(sceneOrder = idx + 1) }
        _currentScenes.value = reindexed
        viewModelScope.launch {
            repository.saveScenes(reindexed)
        }
    }

    fun deleteScene(sceneId: String) {
        saveSnapshotForUndo()
        val current = _currentScenes.value.filter { it.id != sceneId }
        val reindexed = current.mapIndexed { idx, sc -> sc.copy(sceneOrder = idx + 1) }
        _currentScenes.value = reindexed
        viewModelScope.launch {
            repository.deleteScene(sceneId)
            repository.saveScenes(reindexed)
        }
    }

    fun moveScene(fromIndex: Int, toIndex: Int) {
        if (fromIndex !in _currentScenes.value.indices || toIndex !in _currentScenes.value.indices) return
        saveSnapshotForUndo()
        val mutable = _currentScenes.value.toMutableList()
        val item = mutable.removeAt(fromIndex)
        mutable.add(toIndex, item)
        val reindexed = mutable.mapIndexed { idx, sc -> sc.copy(sceneOrder = idx + 1) }
        _currentScenes.value = reindexed
        viewModelScope.launch {
            repository.saveScenes(reindexed)
        }
    }

    private fun saveSnapshotForUndo() {
        undoStack.push(_currentScenes.value)
        redoStack.clear()
    }

    fun undo() {
        if (undoStack.isNotEmpty()) {
            redoStack.push(_currentScenes.value)
            val prev = undoStack.pop()
            _currentScenes.value = prev
            viewModelScope.launch {
                repository.saveScenes(prev)
            }
        }
    }

    fun redo() {
        if (redoStack.isNotEmpty()) {
            undoStack.push(_currentScenes.value)
            val next = redoStack.pop()
            _currentScenes.value = next
            viewModelScope.launch {
                repository.saveScenes(next)
            }
        }
    }

    fun updateProjectSettings(
        canvasRatio: CanvasRatio? = null,
        quality: VideoQuality? = null,
        fps: VideoFps? = null,
        style: VisualStyle? = null,
        voiceId: String? = null,
        voiceSpeed: Float? = null,
        musicTrackId: String? = null,
        musicVolume: Float? = null,
        subtitlesEnabled: Boolean? = null,
        subtitlePosition: String? = null
    ) {
        val proj = _currentProject.value ?: return
        val updated = proj.copy(
            canvasRatio = canvasRatio ?: proj.canvasRatio,
            quality = quality ?: proj.quality,
            fps = fps ?: proj.fps,
            visualStyle = style ?: proj.visualStyle,
            voiceId = voiceId ?: proj.voiceId,
            voiceSpeed = voiceSpeed ?: proj.voiceSpeed,
            musicTrackId = musicTrackId ?: proj.musicTrackId,
            musicVolume = musicVolume ?: proj.musicVolume,
            subtitlesEnabled = subtitlesEnabled ?: proj.subtitlesEnabled,
            subtitlePosition = subtitlePosition ?: proj.subtitlePosition,
            updatedAt = System.currentTimeMillis()
        )
        _currentProject.value = updated
        viewModelScope.launch {
            repository.updateProject(updated)
        }
    }

    // Thumbnail Generator
    fun updateThumbnail(thumbnail: AiThumbnail) {
        _currentThumbnail.value = thumbnail
        viewModelScope.launch {
            repository.saveThumbnail(thumbnail)
            ThumbnailEngine.saveThumbnailToFile(getApplication(), thumbnail)
        }
    }

    private val _fastPreviewVideoPath = MutableStateFlow<String?>(null)
    val fastPreviewVideoPath: StateFlow<String?> = _fastPreviewVideoPath.asStateFlow()

    private val _isGeneratingFastPreview = MutableStateFlow(false)
    val isGeneratingFastPreview: StateFlow<Boolean> = _isGeneratingFastPreview.asStateFlow()

    fun generateFastPreview(onComplete: (String?) -> Unit = {}) {
        val proj = _currentProject.value ?: return
        val scenes = _currentScenes.value
        viewModelScope.launch {
            _isGeneratingFastPreview.value = true
            try {
                val previewPath = renderEngine.generateAndExportVideo(
                    project = proj,
                    scenes = scenes,
                    isFastPreview = true
                )
                if (previewPath != null) {
                    _fastPreviewVideoPath.value = previewPath
                    _currentProject.value = _currentProject.value?.copy(videoUrl = previewPath)
                    onComplete(previewPath)
                } else {
                    onComplete(null)
                }
            } finally {
                _isGeneratingFastPreview.value = false
            }
        }
    }

    // Video Exporting
    fun startExport(onFinished: (String?) -> Unit = {}) {
        val proj = _currentProject.value ?: return
        val scenes = _currentScenes.value
        viewModelScope.launch {
            repository.updateProjectStatus(proj.id, ProjectStatus.RENDERING, 5)
            val resultPath = renderEngine.generateAndExportVideo(proj, scenes)
            if (resultPath != null) {
                repository.updateProjectStatus(proj.id, ProjectStatus.COMPLETED, 100, resultPath)
                _currentProject.value = _currentProject.value?.copy(
                    status = ProjectStatus.COMPLETED,
                    exportPath = resultPath,
                    progressPercent = 100
                )
                onFinished(resultPath)
            } else {
                repository.updateProjectStatus(proj.id, ProjectStatus.FAILED, 0)
                _currentProject.value = _currentProject.value?.copy(status = ProjectStatus.FAILED)
                onFinished(null)
            }
        }
    }

    fun cancelExport() {
        renderEngine.cancelExport()
        val proj = _currentProject.value ?: return
        viewModelScope.launch {
            repository.updateProjectStatus(proj.id, ProjectStatus.DRAFT, 0)
            _currentProject.value = _currentProject.value?.copy(status = ProjectStatus.DRAFT)
        }
    }

    // Settings & Admin
    fun setAppMode(mode: StudioAppMode) {
        viewModelScope.launch {
            preferencesRepository.setAppMode(mode)
        }
    }

    fun setApiProvider(provider: AiProviderType) {
        viewModelScope.launch {
            preferencesRepository.setProvider(provider)
        }
    }

    fun setBackendUrl(url: String) {
        viewModelScope.launch {
            preferencesRepository.setBackendUrl(url)
        }
    }

    fun setApiKey(key: String) {
        viewModelScope.launch {
            preferencesRepository.setApiKey(key)
        }
    }

    fun testBackendConnection() {
        viewModelScope.launch {
            val config = apiConfig.value
            _generationMessage.value = "Testing connection to ${config.backendBaseUrl}..."
            try {
                val api = AiBackendApi.create(config.backendBaseUrl)
                val response = api.checkHealth()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    preferencesRepository.setConnectionStatus(true)
                    _generationMessage.value = "Connected to Backend v${body.version}! FFmpeg: ${if (body.ffmpegAvailable) "Ready" else "Not Installed"}"
                } else {
                    preferencesRepository.setConnectionStatus(false)
                    _generationMessage.value = "Backend returned code ${response.code()}"
                }
            } catch (e: Exception) {
                preferencesRepository.setConnectionStatus(false)
                _generationMessage.value = "Connection failed: ${e.localizedMessage}. Operating in Mode A (Local Engine)."
            }
        }
    }

    val apkUpdateManager = com.example.updater.ApkUpdateManager(application)
    val updateDownloadState = apkUpdateManager.downloadState

    val appVersionName: String = "1.0"
    val appVersionCode: Int = 1

    fun checkForUpdates() {
        viewModelScope.launch {
            val config = apiConfig.value
            apkUpdateManager.checkForUpdates(config.backendBaseUrl, appVersionName)
        }
    }

    fun startApkDownload(downloadUrl: String, sha256: String?, totalSizeBytes: Long) {
        viewModelScope.launch {
            val config = apiConfig.value
            apkUpdateManager.downloadApk(config.backendBaseUrl, downloadUrl, sha256, totalSizeBytes)
        }
    }

    fun pauseApkDownload() {
        apkUpdateManager.pauseDownload()
    }

    fun installDownloadedApk(file: File) {
        apkUpdateManager.installApk(file)
    }

    fun resetUpdateState() {
        apkUpdateManager.resetState()
    }

    fun clearAppCache(): Long {
        var bytesFreed = 0L
        try {
            val cacheDir = getApplication<Application>().cacheDir
            bytesFreed += deleteDirContents(cacheDir)
            val extCache = getApplication<Application>().externalCacheDir
            if (extCache != null) {
                bytesFreed += deleteDirContents(extCache)
            }
        } catch (e: Exception) {
            // Ignored
        }
        return bytesFreed
    }

    private fun deleteDirContents(dir: File?): Long {
        if (dir == null || !dir.exists() || !dir.isDirectory) return 0L
        var total = 0L
        dir.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                total += deleteDirContents(file)
                file.delete()
            } else {
                total += file.length()
                file.delete()
            }
        }
        return total
    }

    fun getStorageMetrics(): Pair<String, String> {
        val app = getApplication<Application>()
        val cacheSize = (app.cacheDir.walkTopDown().filter { it.isFile }.map { it.length() }.sum() +
                (app.externalCacheDir?.walkTopDown()?.filter { it.isFile }?.map { it.length() }?.sum() ?: 0L))
        val projectsCount = projects.value.size
        val cacheStr = "%.1f MB".format(cacheSize / (1024f * 1024f))
        val projectsStr = "$projectsCount Saved Project${if (projectsCount != 1) "s" else ""}"
        return Pair(cacheStr, projectsStr)
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_currentProject.value?.id == id) {
                _currentProject.value = null
                _currentScenes.value = emptyList()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.release()
    }
}
