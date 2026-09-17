package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.download.DownloadProgress
import com.example.data.download.MediaDownloadManager
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadedMediaEntity
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.Episode
import com.example.data.model.MediaCatalog
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.data.repository.MediaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

data class ActivePlayback(
    val mediaId: String,
    val title: String,
    val episodeTitle: String? = null,
    val videoUrl: String,
    val isOffline: Boolean = false,
    val startPositionMs: Long = 0L,
    val totalDuration: String = ""
)

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val downloadManager = MediaDownloadManager(application, database.mediaDao())
    private val repository = MediaRepository(database.mediaDao(), downloadManager)

    val catalog: List<MediaItem> = repository.getAllCatalogItems()
    val categories: List<String> = MediaCatalog.categories

    private val _selectedCategory = MutableStateFlow("Tout")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val downloadedList: StateFlow<List<DownloadedMediaEntity>> = repository.downloadedMedia
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloadedIds: StateFlow<Set<String>> = repository.downloadedIds
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val downloadStates: StateFlow<Map<String, DownloadProgress>> = repository.downloadStates

    val favoriteIds: StateFlow<Set<String>> = repository.favoriteIds
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val watchHistory: StateFlow<List<WatchHistoryEntity>> = repository.watchHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalStorageBytes: StateFlow<Long> = repository.totalDownloadedBytes
        .map { it ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _selectedMediaForDetails = MutableStateFlow<MediaItem?>(null)
    val selectedMediaForDetails: StateFlow<MediaItem?> = _selectedMediaForDetails.asStateFlow()

    private val _activePlayback = MutableStateFlow<ActivePlayback?>(null)
    val activePlayback: StateFlow<ActivePlayback?> = _activePlayback.asStateFlow()

    val filteredItems: StateFlow<List<MediaItem>> = combine(
        _selectedCategory,
        _searchQuery
    ) { category, query ->
        catalog.filter { item ->
            val matchesCategory = when (category) {
                "Tout" -> true
                "Films" -> item.type == MediaType.MOVIE
                "Séries" -> item.type == MediaType.SERIES
                "Animation & Famille" -> item.category.contains("Animation", ignoreCase = true)
                "Sci-Fi" -> item.category.contains("Sci-Fi", ignoreCase = true)
                "Documentaire" -> item.category.contains("Documentaire", ignoreCase = true)
                else -> item.category.equals(category, ignoreCase = true)
            }

            val matchesQuery = if (query.isBlank()) true else {
                item.title.contains(query, ignoreCase = true) ||
                        item.synopsis.contains(query, ignoreCase = true) ||
                        item.category.contains(query, ignoreCase = true) ||
                        item.director.contains(query, ignoreCase = true)
            }

            matchesCategory && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), catalog)

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun openDetails(item: MediaItem) {
        _selectedMediaForDetails.value = item
    }

    fun closeDetails() {
        _selectedMediaForDetails.value = null
    }

    fun playMedia(media: MediaItem, episode: Episode? = null) {
        val downloadId = episode?.id ?: media.id
        viewModelScope.launch {
            val download = repository.getDownloadById(downloadId)
            val history = repository.getWatchHistoryItem(downloadId)
            val startPos = history?.playbackPositionMs ?: 0L

            val isOfflineValid = download != null && File(download.localFilePath).exists()
            val playbackUrl = if (isOfflineValid) {
                android.net.Uri.fromFile(File(download!!.localFilePath)).toString()
            } else {
                episode?.videoUrl ?: media.videoUrl
            }

            _activePlayback.value = ActivePlayback(
                mediaId = downloadId,
                title = media.title,
                episodeTitle = episode?.title,
                videoUrl = playbackUrl,
                isOffline = isOfflineValid,
                startPositionMs = startPos,
                totalDuration = episode?.duration ?: media.duration
            )
        }
    }

    fun playOfflineDownload(download: DownloadedMediaEntity) {
        viewModelScope.launch {
            val history = repository.getWatchHistoryItem(download.id)
            val startPos = history?.playbackPositionMs ?: 0L

            _activePlayback.value = ActivePlayback(
                mediaId = download.id,
                title = download.title,
                episodeTitle = download.episodeTitle,
                videoUrl = android.net.Uri.fromFile(File(download.localFilePath)).toString(),
                isOffline = true,
                startPositionMs = startPos,
                totalDuration = download.duration
            )
        }
    }

    fun closePlayer() {
        _activePlayback.value = null
    }

    fun saveProgress(positionMs: Long, durationMs: Long) {
        val playback = _activePlayback.value ?: return
        viewModelScope.launch {
            repository.savePlaybackProgress(
                mediaId = playback.mediaId,
                title = playback.title,
                episodeTitle = playback.episodeTitle,
                positionMs = positionMs,
                durationMs = durationMs
            )
        }
    }

    fun toggleFavorite(mediaId: String) {
        viewModelScope.launch {
            val isFav = favoriteIds.value.contains(mediaId)
            repository.toggleFavorite(mediaId, isFav)
        }
    }

    fun startDownload(media: MediaItem, episode: Episode? = null, quality: String = "HD 1080p") {
        repository.startDownload(media, episode, quality)
    }

    fun cancelDownload(downloadId: String) {
        repository.cancelDownload(downloadId)
    }

    fun deleteDownload(downloadId: String) {
        viewModelScope.launch {
            repository.deleteDownload(downloadId)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearWatchHistory()
        }
    }
}
