package com.example.data.remote

import com.example.data.model.AdminMetrics
import com.example.data.model.GenerationJob
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class ScriptGenRequest(
    val prompt: String,
    val language: String,
    val genre: String,
    val durationSeconds: Int,
    val visualStyle: String
)

data class ScriptGenResponse(
    val success: Boolean,
    val hook: String,
    val fullScript: String,
    val suggestedTitle: String,
    val scenes: List<RemoteScenePayload>
)

data class RemoteScenePayload(
    val sceneOrder: Int,
    val durationSeconds: Int,
    val title: String,
    val visualPrompt: String,
    val narration: String,
    val cameraAngle: String,
    val textOverlay: String
)

data class CreateJobRequest(
    val projectId: String,
    val title: String,
    val provider: String,
    val canvasRatio: String,
    val quality: String,
    val scenes: List<RemoteScenePayload>
)

data class JobResponse(
    val success: Boolean,
    val jobId: String,
    val status: String,
    val progress: Int,
    val downloadUrl: String? = null,
    val errorMessage: String? = null
)

data class ServerHealthResponse(
    val status: String,
    val version: String,
    val activeProviders: List<String>,
    val ffmpegAvailable: Boolean
)

data class AppUpdateResponse(
    val available: Boolean,
    val hasUpdate: Boolean = false,
    val message: String? = null,
    val currentVersion: String = "1.0",
    val latestVersion: String = "2.4.0",
    val versionCode: Int = 240,
    val downloadUrl: String? = null,
    val sizeBytes: Long = 0L,
    val sizeFormatted: String? = null,
    val sha256Checksum: String? = null,
    val releaseDate: String? = null,
    val releaseNotes: List<String> = emptyList()
)

interface AiBackendApi {
    @GET("/api/health")
    suspend fun checkHealth(): Response<ServerHealthResponse>

    @GET("/api/updates/check")
    suspend fun checkAppUpdate(
        @retrofit2.http.Query("currentVersion") currentVersion: String
    ): Response<AppUpdateResponse>

    @POST("/api/ai/script")
    suspend fun generateScript(
        @Header("Authorization") authHeader: String?,
        @Body request: ScriptGenRequest
    ): Response<ScriptGenResponse>

    @POST("/api/ai/video-job")
    suspend fun submitVideoJob(
        @Header("Authorization") authHeader: String?,
        @Body request: CreateJobRequest
    ): Response<JobResponse>

    @GET("/api/ai/video-job/{jobId}")
    suspend fun getJobStatus(
        @Header("Authorization") authHeader: String?,
        @Path("jobId") jobId: String
    ): Response<JobResponse>

    @GET("/api/admin/metrics")
    suspend fun getAdminMetrics(
        @Header("Authorization") authHeader: String?
    ): Response<AdminMetrics>

    companion object {
        fun create(baseUrl: String): AiBackendApi {
            val validUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(validUrl)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(AiBackendApi::class.java)
        }
    }
}
