package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.download.DownloadProgress
import com.example.data.model.Episode
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaGreen
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaSurfaceDark
import com.example.ui.theme.CinemaSurfaceLightDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaDetailSheet(
    media: MediaItem,
    isDownloaded: Boolean,
    isFavorite: Boolean,
    downloadStates: Map<String, DownloadProgress>,
    downloadedIds: Set<String>,
    onDismiss: () -> Unit,
    onPlay: (media: MediaItem, episode: Episode?) -> Unit,
    onStartDownload: (media: MediaItem, episode: Episode?, quality: String) -> Unit,
    onToggleFavorite: (mediaId: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedQuality by remember { mutableStateOf("HD 1080p") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CinemaSurfaceDark,
        dragHandle = null,
        modifier = Modifier.testTag("media_detail_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Header Image Box
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    if (media.localDrawableRes != null) {
                        Image(
                            painter = painterResource(id = media.localDrawableRes),
                            contentDescription = media.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(media.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = media.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Gradients
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.5f),
                                        Color.Transparent,
                                        CinemaSurfaceDark
                                    )
                                )
                            )
                    )

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = Color.White
                        )
                    }

                    // Play icon in center
                    IconButton(
                        onClick = { onPlay(media, null) },
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(CinemaRed.copy(alpha = 0.9f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Lire",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // Title & Meta Info
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = media.title,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = CinemaGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = media.rating.toString(),
                                color = CinemaGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "${media.releaseYear}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "•",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )

                        Text(
                            text = media.duration,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CinemaSurfaceLightDark
                        ) {
                            Text(
                                text = media.quality,
                                color = CinemaGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CinemaGreen.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "GRATUIT",
                                color = CinemaGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Primary Play Button
                    Button(
                        onClick = { onPlay(media, null) },
                        colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("detail_watch_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDownloaded) "Regarder hors-ligne" else "Regarder en streaming",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Action buttons: Download & Watchlist
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val mainDownloadState = downloadStates[media.id]

                        OutlinedButton(
                            onClick = {
                                if (!isDownloaded && mainDownloadState !is DownloadProgress.Downloading) {
                                    onStartDownload(media, null, selectedQuality)
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("detail_download_button")
                        ) {
                            when {
                                isDownloaded -> {
                                    Icon(
                                        imageVector = Icons.Default.FileDownloadDone,
                                        contentDescription = null,
                                        tint = CinemaGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Téléchargé",
                                        color = CinemaGreen,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                mainDownloadState is DownloadProgress.Downloading -> {
                                    CircularProgressIndicator(
                                        progress = { mainDownloadState.progressPercent / 100f },
                                        color = CinemaRed,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${mainDownloadState.progressPercent}%",
                                        color = CinemaRed,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.Download,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Télécharger (${media.downloadFileSizeMb} Mo)",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Bookmark / Watchlist
                        OutlinedButton(
                            onClick = { onToggleFavorite(media.id) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isFavorite) CinemaGold else Color.White
                            ),
                            modifier = Modifier
                                .weight(0.7f)
                                .height(46.dp)
                                .testTag("detail_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isFavorite) CinemaGold else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isFavorite) "Enregistré" else "Ma Liste",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Quality Selector Chips
                    Text(
                        text = "Résolution de téléchargement :",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("HD 720p", "HD 1080p").forEach { quality ->
                            val isSelected = selectedQuality == quality
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) CinemaRed.copy(alpha = 0.25f) else CinemaSurfaceLightDark,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) CinemaRed else Color.Transparent,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { selectedQuality = quality }
                            ) {
                                Text(
                                    text = quality,
                                    color = if (isSelected) CinemaRed else Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Synopsis
                    Text(
                        text = "Synopsis",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = media.synopsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Réalisation & Studio : ${media.director}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // Episodes Section (if series)
            if (media.type == MediaType.SERIES && media.episodes.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = CinemaGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Épisodes (${media.episodes.size})",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                items(media.episodes) { ep ->
                    val epDownloaded = downloadedIds.contains(ep.id)
                    val epDownloadState = downloadStates[ep.id]

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = CinemaSurfaceLightDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clickable { onPlay(media, ep) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Episode number badge
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = CinemaRed.copy(alpha = 0.2f),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${ep.episodeNumber}",
                                        color = CinemaRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = ep.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${ep.duration} • ${ep.downloadFileSizeMb} Mo",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }

                            // Download icon for episode
                            IconButton(
                                onClick = {
                                    if (!epDownloaded && epDownloadState !is DownloadProgress.Downloading) {
                                        onStartDownload(media, ep, selectedQuality)
                                    }
                                }
                            ) {
                                when {
                                    epDownloaded -> {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Téléchargé",
                                            tint = CinemaGreen
                                        )
                                    }
                                    epDownloadState is DownloadProgress.Downloading -> {
                                        CircularProgressIndicator(
                                            progress = { epDownloadState.progressPercent / 100f },
                                            color = CinemaRed,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    else -> {
                                        Icon(
                                            imageVector = Icons.Default.Download,
                                            contentDescription = "Télécharger l'épisode",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }

                            // Play episode button
                            IconButton(
                                onClick = { onPlay(media, ep) }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Lire l'épisode",
                                    tint = CinemaRed
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
