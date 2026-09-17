package com.example.data.model

enum class MediaType {
    MOVIE,
    SERIES,
    SHORT
}

data class Episode(
    val id: String,
    val seriesId: String,
    val episodeNumber: Int,
    val title: String,
    val duration: String,
    val synopsis: String,
    val videoUrl: String,
    val downloadFileSizeMb: Int
)

data class MediaItem(
    val id: String,
    val title: String,
    val type: MediaType,
    val synopsis: String,
    val category: String,
    val duration: String,
    val releaseYear: Int,
    val rating: Float,
    val videoUrl: String,
    val downloadFileSizeMb: Int,
    val localDrawableRes: Int? = null,
    val thumbnailUrl: String = "",
    val director: String = "Cinéma Libre",
    val quality: String = "1080p Full HD",
    val episodes: List<Episode> = emptyList()
)
