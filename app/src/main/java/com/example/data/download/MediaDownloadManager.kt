package com.example.data.download

import android.content.Context
import android.os.Environment
import com.example.data.local.DownloadedMediaEntity
import com.example.data.local.MediaDao
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

sealed class DownloadProgress {
    object Idle : DownloadProgress()
    data class Downloading(
        val mediaId: String,
        val title: String,
        val progressPercent: Int,
        val downloadedBytes: Long,
        val totalBytes: Long,
        val speedMbPerSec: Double
    ) : DownloadProgress()
    data class Completed(val mediaId: String, val filePath: String) : DownloadProgress()
    data class Failed(val mediaId: String, val error: String) : DownloadProgress()
}

class MediaDownloadManager(
    private val context: Context,
    private val mediaDao: MediaDao
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val scope = CoroutineScope(Dispatchers.IO)
    private val activeJobs = ConcurrentHashMap<String, Job>()

    private val _downloadStates = MutableStateFlow<Map<String, DownloadProgress>>(emptyMap())
    val downloadStates: StateFlow<Map<String, DownloadProgress>> = _downloadStates.asStateFlow()

    private fun getStorageDirectory(): File {
        val external = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        return external ?: File(context.filesDir, "movies").apply { mkdirs() }
    }

    fun startDownload(media: MediaItem, episode: Episode? = null, quality: String = "HD 1080p") {
        val downloadId = episode?.id ?: media.id
        val downloadUrl = episode?.videoUrl ?: media.videoUrl
        val title = episode?.title ?: media.title
        val episodeTitle = if (episode != null) episode.title else null
        val duration = episode?.duration ?: media.duration

        if (activeJobs.containsKey(downloadId)) return

        val job = scope.launch {
            try {
                updateProgress(
                    downloadId,
                    DownloadProgress.Downloading(
                        mediaId = downloadId,
                        title = title,
                        progressPercent = 0,
                        downloadedBytes = 0L,
                        totalBytes = 0L,
                        speedMbPerSec = 0.0
                    )
                )

                val dir = getStorageDirectory()
                if (!dir.exists()) dir.mkdirs()
                val targetFile = File(dir, "${downloadId}.mp4")

                val request = Request.Builder()
                    .url(downloadUrl)
                    .header("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile) CineLibre/1.0")
                    .build()
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    updateProgress(downloadId, DownloadProgress.Failed(downloadId, "Erreur serveur : ${response.code}"))
                    return@launch
                }

                val body = response.body
                if (body == null) {
                    updateProgress(downloadId, DownloadProgress.Failed(downloadId, "Flux vidéo vide"))
                    return@launch
                }

                val contentLength = body.contentLength()
                val totalBytes = if (contentLength > 0) contentLength else (media.downloadFileSizeMb * 1024L * 1024L)

                val inputStream = body.byteStream()
                val outputStream = FileOutputStream(targetFile)
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                var totalDownloaded = 0L
                var lastTime = System.currentTimeMillis()
                var bytesSinceLast = 0L

                inputStream.use { input ->
                    outputStream.use { output ->
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalDownloaded += bytesRead
                            bytesSinceLast += bytesRead

                            val now = System.currentTimeMillis()
                            val timeDiff = now - lastTime
                            if (timeDiff >= 400) {
                                val speed = if (timeDiff > 0) (bytesSinceLast.toDouble() / (1024 * 1024)) / (timeDiff / 1000.0) else 0.0
                                val percent = if (totalBytes > 0) ((totalDownloaded * 100) / totalBytes).toInt().coerceIn(0, 100) else 0

                                updateProgress(
                                    downloadId,
                                    DownloadProgress.Downloading(
                                        mediaId = downloadId,
                                        title = title,
                                        progressPercent = percent,
                                        downloadedBytes = totalDownloaded,
                                        totalBytes = totalBytes,
                                        speedMbPerSec = (speed * 10).toInt() / 10.0
                                    )
                                )
                                lastTime = now
                                bytesSinceLast = 0L
                            }
                        }
                    }
                }

                // Insert into Room
                val entity = DownloadedMediaEntity(
                    id = downloadId,
                    mediaId = media.id,
                    title = media.title,
                    episodeTitle = episodeTitle,
                    thumbnailUrl = media.thumbnailUrl,
                    localFilePath = targetFile.absolutePath,
                    fileSizeBytes = targetFile.length(),
                    downloadTimestamp = System.currentTimeMillis(),
                    duration = duration,
                    quality = quality,
                    type = media.type.name
                )
                mediaDao.insertDownload(entity)

                updateProgress(downloadId, DownloadProgress.Completed(downloadId, targetFile.absolutePath))
            } catch (e: Exception) {
                updateProgress(downloadId, DownloadProgress.Failed(downloadId, e.localizedMessage ?: "Échec du téléchargement"))
            } finally {
                activeJobs.remove(downloadId)
            }
        }

        activeJobs[downloadId] = job
    }

    fun cancelDownload(downloadId: String) {
        activeJobs[downloadId]?.cancel()
        activeJobs.remove(downloadId)
        _downloadStates.value = _downloadStates.value - downloadId

        // Remove any partial file
        val targetFile = File(getStorageDirectory(), "${downloadId}.mp4")
        if (targetFile.exists()) {
            targetFile.delete()
        }
    }

    suspend fun deleteDownloadedMedia(downloadId: String) = withContext(Dispatchers.IO) {
        val download = mediaDao.getDownloadById(downloadId)
        if (download != null) {
            val file = File(download.localFilePath)
            if (file.exists()) {
                file.delete()
            }
            mediaDao.deleteDownload(downloadId)
        }
        _downloadStates.value = _downloadStates.value - downloadId
    }

    private fun updateProgress(downloadId: String, progress: DownloadProgress) {
        val current = _downloadStates.value.toMutableMap()
        current[downloadId] = progress
        _downloadStates.value = current
    }
}
