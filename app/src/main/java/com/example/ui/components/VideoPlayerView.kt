package com.example.ui.components

import android.net.Uri
import android.view.LayoutInflater
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.mediacodec.MediaCodecSelector
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.example.R
import com.example.ui.theme.CinemaGold
import com.example.ui.theme.CinemaGreen
import com.example.ui.theme.CinemaRed
import com.example.ui.theme.CinemaSurfaceDark
import com.example.ui.viewmodel.ActivePlayback
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerView(
    playback: ActivePlayback,
    onClose: () -> Unit,
    onProgressUpdate: (positionMs: Long, durationMs: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var isPlaying by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var currentPositionMs by remember { mutableLongStateOf(0L) }
    var durationMs by remember { mutableLongStateOf(0L) }
    var showControls by remember { mutableStateOf(true) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }
    var showSpeedMenu by remember { mutableStateOf(false) }

    val exoPlayer = remember(playback.videoUrl) {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Linux; Android 14; Mobile) CineLibre/1.0")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(20000)
            .setReadTimeoutMs(20000)

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        val mediaSourceFactory = DefaultMediaSourceFactory(context)
            .setDataSourceFactory(dataSourceFactory)

        val renderersFactory = DefaultRenderersFactory(context).apply {
            setEnableDecoderFallback(true)
            setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)
            setMediaCodecSelector(MediaCodecSelector.DEFAULT)
        }

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                2000,
                30000,
                1000,
                1500
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        ExoPlayer.Builder(context, renderersFactory)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .build().apply {
                volume = 1.0f
                val mediaItem = MediaItem.fromUri(Uri.parse(playback.videoUrl))
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
                if (playback.startPositionMs > 0) {
                    seekTo(playback.startPositionMs)
                }
            }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        isBuffering = true
                    }
                    Player.STATE_READY -> {
                        isBuffering = false
                        errorMessage = null
                        durationMs = exoPlayer.duration.coerceAtLeast(0L)
                    }
                    Player.STATE_ENDED -> {
                        isBuffering = false
                    }
                    Player.STATE_IDLE -> {
                        // idle
                    }
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                isBuffering = false
                android.util.Log.e("CineLibrePlayer", "Playback error code=${error.errorCodeName} (${error.errorCode})", error)
                errorMessage = error.localizedMessage ?: "Erreur de connexion à la source vidéo"
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.stop()
            exoPlayer.clearMediaItems()
            exoPlayer.release()
        }
    }

    // Periodic progress updates loop
    LaunchedEffect(exoPlayer) {
        while (true) {
            if (exoPlayer.isPlaying) {
                currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L)
                val dur = exoPlayer.duration.coerceAtLeast(0L)
                durationMs = dur
                onProgressUpdate(currentPositionMs, durationMs)
            }
            delay(1000)
        }
    }

    // Auto-hide controls
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying && errorMessage == null) {
            delay(4000)
            showControls = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (errorMessage == null) {
                    showControls = !showControls
                }
            }
            .testTag("video_player_container")
    ) {
        // Video View with TextureView
        AndroidView(
            factory = { ctx ->
                val view = LayoutInflater.from(ctx).inflate(R.layout.layout_player_view, null) as PlayerView
                view.player = exoPlayer
                view
            },
            update = { playerView ->
                if (playerView.player != exoPlayer) {
                    playerView.player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Buffering spinner
        if (isBuffering && errorMessage == null) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = CinemaRed,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(52.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Chargement de la vidéo...",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Error message overlay if loading fails
        if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = CinemaRed,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Impossible de lire la vidéo",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Vérifiez votre connexion internet ou réessayez.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                errorMessage = null
                                isBuffering = true
                                exoPlayer.prepare()
                                exoPlayer.play()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaRed),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Réessayer", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(containerColor = CinemaSurfaceDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Fermer", color = Color.White)
                        }
                    }
                }
            }
        }

        // Bespoke overlay controls
        AnimatedVisibility(
            visible = showControls && errorMessage == null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.85f),
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.90f)
                            )
                        )
                    )
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (playback.isOffline) CinemaGreen.copy(alpha = 0.2f) else CinemaRed.copy(alpha = 0.2f),
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = if (playback.isOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                                        contentDescription = null,
                                        tint = if (playback.isOffline) CinemaGreen else CinemaRed,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (playback.isOffline) "HORS-LIGNE" else "STREAMING",
                                        color = if (playback.isOffline) CinemaGreen else CinemaRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = playback.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        if (!playback.episodeTitle.isNullOrBlank()) {
                            Text(
                                text = playback.episodeTitle,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            exoPlayer.stop()
                            onClose()
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("close_player_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer le lecteur",
                            tint = Color.White
                        )
                    }
                }

                // Center Play/Pause & Seek Buttons
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rewind 10s
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition - 10000).coerceAtLeast(0L)
                            exoPlayer.seekTo(newPos)
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Reculer 10s",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Big Play/Pause
                    IconButton(
                        onClick = {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        },
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(CinemaRed)
                            .testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Lecture",
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    // Forward 10s
                    IconButton(
                        onClick = {
                            val newPos = (exoPlayer.currentPosition + 10000).coerceAtMost(durationMs)
                            exoPlayer.seekTo(newPos)
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Avancer 10s",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Bottom Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Time and speed row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${formatTime(currentPositionMs)} / ${formatTime(durationMs)}",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Box {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier
                                    .clickable { showSpeedMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = null,
                                        tint = CinemaGold,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${playbackSpeed}x",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showSpeedMenu,
                                onDismissRequest = { showSpeedMenu = false }
                            ) {
                                listOf(0.75f, 1.0f, 1.25f, 1.5f, 2.0f).forEach { speed ->
                                    DropdownMenuItem(
                                        text = { Text("${speed}x") },
                                        onClick = {
                                            playbackSpeed = speed
                                            exoPlayer.playbackParameters = PlaybackParameters(speed)
                                            showSpeedMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Seekbar
                    val sliderValue = if (durationMs > 0) {
                        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    Slider(
                        value = sliderValue,
                        onValueChange = { frac ->
                            val targetMs = (frac * durationMs).toLong()
                            currentPositionMs = targetMs
                            exoPlayer.seekTo(targetMs)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = CinemaRed,
                            activeTrackColor = CinemaRed,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
