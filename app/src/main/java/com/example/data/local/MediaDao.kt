package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaDao {
    // Downloads
    @Query("SELECT * FROM downloaded_media ORDER BY downloadTimestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadedMediaEntity>>

    @Query("SELECT * FROM downloaded_media WHERE id = :id LIMIT 1")
    suspend fun getDownloadById(id: String): DownloadedMediaEntity?

    @Query("SELECT id FROM downloaded_media")
    fun getAllDownloadedIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadedMediaEntity)

    @Query("DELETE FROM downloaded_media WHERE id = :id")
    suspend fun deleteDownload(id: String)

    @Query("SELECT SUM(fileSizeBytes) FROM downloaded_media")
    fun getTotalDownloadedBytes(): Flow<Long?>

    // Watch History
    @Query("SELECT * FROM watch_history ORDER BY lastWatchedTimestamp DESC")
    fun getWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE mediaId = :mediaId LIMIT 1")
    suspend fun getWatchHistoryItem(mediaId: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWatchHistory(history: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE mediaId = :mediaId")
    suspend fun deleteWatchHistory(mediaId: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearWatchHistory()

    // Favorites
    @Query("SELECT mediaId FROM favorites ORDER BY addedTimestamp DESC")
    fun getFavoriteIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE mediaId = :mediaId")
    suspend fun removeFavorite(mediaId: String)
}
