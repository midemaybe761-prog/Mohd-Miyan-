package com.example.data.repository

import com.example.data.local.ProjectDao
import com.example.data.local.ProjectEntity
import com.example.data.local.SceneDao
import com.example.data.local.SceneEntity
import com.example.data.local.ThumbnailDao
import com.example.data.local.ThumbnailEntity
import com.example.data.model.CanvasRatio
import com.example.data.model.ProjectStatus
import com.example.data.model.Scene
import com.example.data.model.TransitionType
import com.example.data.model.VideoFps
import com.example.data.model.VideoProject
import com.example.data.model.VideoQuality
import com.example.data.model.VisualStyle
import com.example.data.model.AiThumbnail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class VideoRepository(
    private val projectDao: ProjectDao,
    private val sceneDao: SceneDao,
    private val thumbnailDao: ThumbnailDao
) {
    val allProjects: Flow<List<VideoProject>> = projectDao.getAllProjects().map { list ->
        list.map { it.toDomain() }
    }

    val allThumbnails: Flow<List<AiThumbnail>> = thumbnailDao.getAllThumbnails().map { list ->
        list.map { it.toDomain() }
    }

    fun getScenesForProject(projectId: String): Flow<List<Scene>> {
        return sceneDao.getScenesForProject(projectId).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun getProjectById(id: String): VideoProject? {
        return projectDao.getProjectById(id)?.toDomain()
    }

    fun observeProjectById(id: String): Flow<VideoProject?> {
        return projectDao.observeProjectById(id).map { it?.toDomain() }
    }

    suspend fun saveProject(project: VideoProject) {
        projectDao.insertProject(ProjectEntity.fromDomain(project))
    }

    suspend fun updateProject(project: VideoProject) {
        projectDao.updateProject(ProjectEntity.fromDomain(project))
    }

    suspend fun updateProjectStatus(id: String, status: ProjectStatus, progress: Int, exportPath: String? = null) {
        projectDao.updateProjectStatus(id, status.name, progress, exportPath)
    }

    suspend fun deleteProject(id: String) {
        projectDao.deleteProject(id)
        sceneDao.deleteScenesForProject(id)
    }

    suspend fun saveScenes(scenes: List<Scene>) {
        sceneDao.insertScenes(scenes.map { SceneEntity.fromDomain(it) })
    }

    suspend fun saveScene(scene: Scene) {
        sceneDao.insertScene(SceneEntity.fromDomain(scene))
    }

    suspend fun updateScene(scene: Scene) {
        sceneDao.updateScene(SceneEntity.fromDomain(scene))
    }

    suspend fun deleteScene(sceneId: String) {
        sceneDao.deleteScene(sceneId)
    }

    suspend fun getScenesList(projectId: String): List<Scene> {
        return sceneDao.getScenesList(projectId).map { it.toDomain() }
    }

    suspend fun saveThumbnail(thumbnail: AiThumbnail) {
        thumbnailDao.insertThumbnail(ThumbnailEntity.fromDomain(thumbnail))
    }

    suspend fun getThumbnailForProject(projectId: String): AiThumbnail? {
        return thumbnailDao.getThumbnailForProject(projectId)?.toDomain()
    }

    suspend fun seedInitialDataIfEmpty() {
        if (projectDao.getProjectsCount() == 0) {
            val sampleId = UUID.randomUUID().toString()
            val sampleProject = VideoProject(
                id = sampleId,
                title = "Future of Artificial Intelligence 2026",
                prompt = "A high-energy documentary on how generative AI and neural robotics will transform human civilization by 2030.",
                fullScript = "Hook: What if machines could imagine worlds before they even exist?\nScene 1: Across the globe, neural clusters process quadrillions of operations per second.\nScene 2: Autonomous androids assist in medical breakthroughs and deep space exploration.\nScene 3: The boundary between imagination and reality dissolves forever.",
                scriptLanguage = "English",
                durationSeconds = 60,
                canvasRatio = CanvasRatio.VERTICAL_9_16,
                quality = VideoQuality.HD_720P,
                fps = VideoFps.FPS_30,
                visualStyle = VisualStyle.CYBERPUNK,
                voiceId = "en_male_marcus",
                musicTrackId = "cyber_pulse",
                status = ProjectStatus.COMPLETED,
                progressPercent = 100
            )
            projectDao.insertProject(ProjectEntity.fromDomain(sampleProject))

            val sampleScenes = listOf(
                Scene(
                    projectId = sampleId,
                    sceneOrder = 1,
                    durationSeconds = 5,
                    title = "Opening Hook",
                    visualPrompt = "Hyper-detailed glowing neural network core pulsing in deep space, holographic circuits",
                    narration = "What if machines could imagine worlds before they even exist?",
                    cameraAngle = "Slow forward dolly zoom into quantum core",
                    transition = TransitionType.FLASH,
                    textOverlay = "THE AI REVOLUTION",
                    subtitleText = "What if machines could imagine worlds before they even exist?"
                ),
                Scene(
                    projectId = sampleId,
                    sceneOrder = 2,
                    durationSeconds = 7,
                    title = "Quantum Datacenters",
                    visualPrompt = "Futuristic underwater server farm cooling towers with electric cyan illumination",
                    narration = "Across the globe, neural clusters process quadrillions of operations per second.",
                    cameraAngle = "Wide panoramic orbital tilt",
                    transition = TransitionType.CROSS_DISSOLVE,
                    textOverlay = "10^18 FLOPS COMPUTING",
                    subtitleText = "Across the globe, neural clusters process quadrillions of operations per second."
                ),
                Scene(
                    projectId = sampleId,
                    sceneOrder = 3,
                    durationSeconds = 8,
                    title = "Humanity's Next Leap",
                    visualPrompt = "Robotic hand gently shaking hands with human scientist in sunlit futuristic laboratory",
                    narration = "The boundary between human creativity and artificial intelligence is dissolving forever.",
                    cameraAngle = "Cinematic shallow depth of field close up",
                    transition = TransitionType.FADE,
                    textOverlay = "HUMAN + AI SYMBIOSIS",
                    subtitleText = "The boundary between human creativity and artificial intelligence is dissolving forever."
                )
            )
            sceneDao.insertScenes(sampleScenes.map { SceneEntity.fromDomain(it) })
        }
    }
}
