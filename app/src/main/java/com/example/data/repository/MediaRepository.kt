package com.example.data.repository

import com.example.data.download.DownloadProgress
import com.example.data.download.MediaDownloadManager
import com.example.data.local.DownloadedMediaEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.MediaDao
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.Episode
import com.example.data.model.MediaCatalog
import com.example.data.model.MediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class MediaRepository(
    private val mediaDao: MediaDao,
    private val downloadManager: MediaDownloadManager
) {
    fun getAllCatalogItems(): List<MediaItem> = MediaCatalog.items

    fun getItemById(id: String): MediaItem? = MediaCatalog.items.firstOrNull { it.id == id }

    val downloadedMedia: Flow<List<DownloadedMediaEntity>> = mediaDao.getAllDownloads()
    val downloadedIds: Flow<List<String>> = mediaDao.getAllDownloadedIds()
    val totalDownloadedBytes: Flow<Long?> = mediaDao.getTotalDownloadedBytes()
    val downloadStates: StateFlow<Map<String, DownloadProgress>> = downloadManager.downloadStates

    val watchHistory: Flow<List<WatchHistoryEntity>> = mediaDao.getWatchHistory()
    val favoriteIds: Flow<List<String>> = mediaDao.getFavoriteIds()

    fun startDownload(media: MediaItem, episode: Episode? = null, quality: String = "HD 1080p") {
        downloadManager.startDownload(media, episode, quality)
    }

    fun cancelDownload(downloadId: String) {
        downloadManager.cancelDownload(downloadId)
    }

    suspend fun deleteDownload(downloadId: String) {
        downloadManager.deleteDownloadedMedia(downloadId)
    }

    suspend fun getDownloadById(downloadId: String): DownloadedMediaEntity? {
        return mediaDao.getDownloadById(downloadId)
    }

    suspend fun toggleFavorite(mediaId: String, isFavorite: Boolean) {
        if (isFavorite) {
            mediaDao.removeFavorite(mediaId)
        } else {
            mediaDao.addFavorite(FavoriteEntity(mediaId))
        }
    }

    suspend fun savePlaybackProgress(mediaId: String, title: String, episodeTitle: String?, positionMs: Long, durationMs: Long) {
        if (durationMs > 0) {
            mediaDao.saveWatchHistory(
                WatchHistoryEntity(
                    mediaId = mediaId,
                    title = title,
                    episodeTitle = episodeTitle,
                    playbackPositionMs = positionMs,
                    durationMs = durationMs
                )
            )
        }
    }

    suspend fun getWatchHistoryItem(mediaId: String): WatchHistoryEntity? {
        return mediaDao.getWatchHistoryItem(mediaId)
    }

    suspend fun clearWatchHistory() {
        mediaDao.clearWatchHistory()
    }
}
