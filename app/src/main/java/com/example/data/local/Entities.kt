package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_media")
data class DownloadedMediaEntity(
    @PrimaryKey val id: String, // mediaId or episodeId
    val mediaId: String,
    val title: String,
    val episodeTitle: String? = null,
    val thumbnailUrl: String,
    val localFilePath: String,
    val fileSizeBytes: Long,
    val downloadTimestamp: Long = System.currentTimeMillis(),
    val duration: String,
    val quality: String = "HD 1080p",
    val type: String // "MOVIE", "SERIES", "SHORT"
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val mediaId: String,
    val title: String,
    val episodeTitle: String? = null,
    val playbackPositionMs: Long,
    val durationMs: Long,
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val mediaId: String,
    val addedTimestamp: Long = System.currentTimeMillis()
)
