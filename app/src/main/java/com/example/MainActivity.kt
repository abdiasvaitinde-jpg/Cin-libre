package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.download.DownloadProgress
import com.example.ui.components.VideoPlayerView
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MediaDetailSheet
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.WatchlistScreen
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaSurfaceDark
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MediaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MediaViewModel = viewModel()) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val catalog = viewModel.catalog
    val categories = viewModel.categories
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filteredItems by viewModel.filteredItems.collectAsStateWithLifecycle()
    val downloadedList by viewModel.downloadedList.collectAsStateWithLifecycle()
    val downloadedIds by viewModel.downloadedIds.collectAsStateWithLifecycle()
    val downloadStates by viewModel.downloadStates.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val watchHistory by viewModel.watchHistory.collectAsStateWithLifecycle()
    val totalStorageBytes by viewModel.totalStorageBytes.collectAsStateWithLifecycle()

    val selectedMediaForDetails by viewModel.selectedMediaForDetails.collectAsStateWithLifecycle()
    val activePlayback by viewModel.activePlayback.collectAsStateWithLifecycle()

    val activeDownloadsCount = downloadStates.values.count { it is DownloadProgress.Downloading }

    // Intercept back button if player or details open
    BackHandler(enabled = activePlayback != null || selectedMediaForDetails != null || selectedTab != 0) {
        when {
            activePlayback != null -> viewModel.closePlayer()
            selectedMediaForDetails != null -> viewModel.closeDetails()
            selectedTab != 0 -> selectedTab = 0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                // Hide navigation bar when video player is active
                if (activePlayback == null) {
                    NavigationBar(
                        containerColor = CinemaSurfaceDark,
                        contentColor = Color.White,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("main_navigation_bar")
                    ) {
                        // 0: Accueil
                        NavigationBarItem(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == 0) Icons.Default.Home else Icons.Outlined.Home,
                                    contentDescription = "Accueil"
                                )
                            },
                            label = { Text("Accueil", fontSize = 11.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CinemaRed,
                                selectedTextColor = CinemaRed,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = CinemaRed.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_tab_home")
                        )

                        // 1: Téléchargements
                        NavigationBarItem(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (activeDownloadsCount > 0) {
                                            Badge(containerColor = CinemaRed) {
                                                Text("$activeDownloadsCount")
                                            }
                                        } else if (downloadedList.isNotEmpty()) {
                                            Badge(containerColor = CinemaGold) {
                                                Text("${downloadedList.size}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (selectedTab == 1) Icons.Default.Download else Icons.Outlined.Download,
                                        contentDescription = "Téléchargements"
                                    )
                                }
                            },
                            label = { Text("Téléchargements", fontSize = 11.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CinemaRed,
                                selectedTextColor = CinemaRed,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = CinemaRed.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_tab_downloads")
                        )

                        // 2: Recherche
                        NavigationBarItem(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == 2) Icons.Default.Search else Icons.Outlined.Search,
                                    contentDescription = "Recherche"
                                )
                            },
                            label = { Text("Recherche", fontSize = 11.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CinemaRed,
                                selectedTextColor = CinemaRed,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = CinemaRed.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_tab_search")
                        )

                        // 3: Ma Liste / Bibliothèque
                        NavigationBarItem(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == 3) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Ma Liste"
                                )
                            },
                            label = { Text("Ma Liste", fontSize = 11.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = CinemaRed,
                                selectedTextColor = CinemaRed,
                                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                                indicatorColor = CinemaRed.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier.testTag("nav_tab_watchlist")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        catalog = catalog,
                        filteredCatalog = filteredItems,
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelect = { viewModel.selectCategory(it) },
                        watchHistory = watchHistory,
                        downloadedIds = downloadedIds,
                        onMediaClick = { viewModel.openDetails(it) },
                        onPlayMedia = { viewModel.playMedia(it) },
                        onDownloadClick = { viewModel.startDownload(it) },
                        onSearchClick = { selectedTab = 2 }
                    )
                    1 -> DownloadsScreen(
                        downloadedList = downloadedList,
                        downloadStates = downloadStates,
                        totalStorageBytes = totalStorageBytes,
                        onPlayDownloaded = { viewModel.playOfflineDownload(it) },
                        onDeleteDownloaded = { viewModel.deleteDownload(it) },
                        onCancelDownload = { viewModel.cancelDownload(it) },
                        onNavigateToHome = { selectedTab = 0 }
                    )
                    2 -> SearchScreen(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelect = { viewModel.selectCategory(it) },
                        results = filteredItems,
                        downloadedIds = downloadedIds,
                        onMediaClick = { viewModel.openDetails(it) },
                        onPlayClick = { viewModel.playMedia(it) },
                        onDownloadClick = { viewModel.startDownload(it) }
                    )
                    3 -> WatchlistScreen(
                        favoriteItems = catalog.filter { favoriteIds.contains(it.id) },
                        watchHistory = watchHistory,
                        catalog = catalog,
                        downloadedIds = downloadedIds,
                        onMediaClick = { viewModel.openDetails(it) },
                        onPlayMedia = { viewModel.playMedia(it) },
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }
            }
        }

        // Media Detail Bottom Sheet
        val detailsItem = selectedMediaForDetails
        if (detailsItem != null) {
            MediaDetailSheet(
                media = detailsItem,
                isDownloaded = downloadedIds.contains(detailsItem.id),
                isFavorite = favoriteIds.contains(detailsItem.id),
                downloadStates = downloadStates,
                downloadedIds = downloadedIds,
                onDismiss = { viewModel.closeDetails() },
                onPlay = { media, episode ->
                    viewModel.closeDetails()
                    viewModel.playMedia(media, episode)
                },
                onStartDownload = { media, episode, quality ->
                    viewModel.startDownload(media, episode, quality)
                },
                onToggleFavorite = { viewModel.toggleFavorite(it) }
            )
        }

        // Full Screen Video Player Overlay
        val playback = activePlayback
        AnimatedVisibility(
            visible = playback != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            if (playback != null) {
                VideoPlayerView(
                    playback = playback,
                    onClose = { viewModel.closePlayer() },
                    onProgressUpdate = { pos, dur -> viewModel.saveProgress(pos, dur) },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
