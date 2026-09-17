package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.local.WatchHistoryEntity
import com.example.data.model.MediaItem
import com.example.data.model.MediaType
import com.example.ui.components.MediaCard
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaGreen
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaSurfaceDark
import com.example.ui.theme.CinemaSurfaceLightDark

@Composable
fun HomeScreen(
    catalog: List<MediaItem>,
    filteredCatalog: List<MediaItem>,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit,
    watchHistory: List<WatchHistoryEntity>,
    downloadedIds: Set<String>,
    onMediaClick: (MediaItem) -> Unit,
    onPlayMedia: (MediaItem) -> Unit,
    onDownloadClick: (MediaItem) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heroItem = catalog.firstOrNull { it.id == "cosmos_odyssey" } ?: catalog.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen")
    ) {
        // App Top Bar with Brand & Search action
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "Logo CinéLibre",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Ciné",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Libre",
                                color = CinemaRed,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = "Films & Séries Gratuits",
                            color = CinemaGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (downloadedIds.isNotEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CinemaGreen.copy(alpha = 0.15f),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = null,
                                    tint = CinemaGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${downloadedIds.size} hors-ligne",
                                    color = CinemaGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onSearchClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CinemaSurfaceDark)
                            .testTag("home_search_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Rechercher",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Hero Featured Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clickable { onMediaClick(heroItem) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_cosmos),
                    contentDescription = heroItem.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Cinematic vignette gradients
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                // Hero content details
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = CinemaRed
                        ) {
                            Text(
                                text = "À LA UNE",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${heroItem.category} • ${heroItem.duration} • ${heroItem.quality}",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = heroItem.title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = heroItem.synopsis,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onPlayMedia(heroItem) },
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("hero_play_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (downloadedIds.contains(heroItem.id)) "Regarder (Hors-ligne)" else "Regarder",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { onMediaClick(heroItem) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Détails & Épisodes",
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) CinemaRed else CinemaSurfaceDark,
                        modifier = Modifier
                            .clickable { onCategorySelect(category) }
                            .testTag("category_chip_$category")
                    ) {
                        Text(
                            text = category,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Section: "Reprendre la lecture" (Watch History)
        if (watchHistory.isNotEmpty() && selectedCategory == "Tout") {
            item {
                SectionHeader(title = "Reprendre la lecture")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(watchHistory) { hist ->
                        val matching = catalog.firstOrNull { it.id == hist.mediaId || it.episodes.any { ep -> ep.id == hist.mediaId } }
                        if (matching != null) {
                            ResumeCard(
                                history = hist,
                                media = matching,
                                onClick = { onPlayMedia(matching) }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Section: Filtered Catalog or "Populaires"
        if (selectedCategory != "Tout") {
            item {
                SectionHeader(title = "Catalogue $selectedCategory (${filteredCatalog.size})")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredCatalog, key = { it.id }) { item ->
                        MediaCard(
                            media = item,
                            isDownloaded = downloadedIds.contains(item.id),
                            onCardClick = { onMediaClick(item) },
                            onPlayClick = { onPlayMedia(item) },
                            onDownloadClick = { onDownloadClick(item) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            // Section 1: Films populaires
            val movies = catalog.filter { it.type == MediaType.MOVIE }
            item {
                SectionHeader(title = "Films populaires")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movies, key = { it.id }) { item ->
                        MediaCard(
                            media = item,
                            isDownloaded = downloadedIds.contains(item.id),
                            onCardClick = { onMediaClick(item) },
                            onPlayClick = { onPlayMedia(item) },
                            onDownloadClick = { onDownloadClick(item) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Section 2: Séries & Mini-séries
            val series = catalog.filter { it.type == MediaType.SERIES }
            item {
                SectionHeader(title = "Séries gratuites complètes")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(series, key = { it.id }) { item ->
                        MediaCard(
                            media = item,
                            isDownloaded = downloadedIds.contains(item.id),
                            onCardClick = { onMediaClick(item) },
                            onPlayClick = { onPlayMedia(item) },
                            onDownloadClick = { onDownloadClick(item) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Banner promo: Animation & Fantastique
            item {
                val animationHero = catalog.firstOrNull { it.id == "sintel_quest" }
                if (animationHero != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CinemaSurfaceDark),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onMediaClick(animationHero) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_animation),
                                contentDescription = animationHero.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 110.dp, height = 75.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = CinemaGold.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "ANIMATION PHARE",
                                        color = CinemaGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = animationHero.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Téléchargeable en 1 clic pour visionnage hors-ligne",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            IconButton(
                                onClick = { onPlayMedia(animationHero) },
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CinemaRed)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Regarder",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Section 3: Documentaires & Courts-métrages
            val shortsAndDocs = catalog.filter { it.type == MediaType.SHORT || it.category.contains("Documentaire") }
            item {
                SectionHeader(title = "Documentaires & Courts-métrages")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(shortsAndDocs, key = { it.id }) { item ->
                        MediaCard(
                            media = item,
                            isDownloaded = downloadedIds.contains(item.id),
                            onCardClick = { onMediaClick(item) },
                            onPlayClick = { onPlayMedia(item) },
                            onDownloadClick = { onDownloadClick(item) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ResumeCard(
    history: WatchHistoryEntity,
    media: MediaItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CinemaSurfaceDark),
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Reprendre",
                        tint = Color.White,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CinemaRed.copy(alpha = 0.9f))
                            .padding(6.dp)
                    )
                }

                // Progress Bar at bottom of poster
                val progress = if (history.durationMs > 0) {
                    (history.playbackPositionMs.toFloat() / history.durationMs.toFloat()).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    color = CinemaRed,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter)
                )
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = history.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!history.episodeTitle.isNullOrBlank()) {
                    Text(
                        text = history.episodeTitle,
                        color = CinemaGold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
