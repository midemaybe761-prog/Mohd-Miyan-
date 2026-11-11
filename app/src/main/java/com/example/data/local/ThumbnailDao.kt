package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ThumbnailDao {
    @Query("SELECT * FROM thumbnails ORDER BY createdAt DESC")
    fun getAllThumbnails(): Flow<List<ThumbnailEntity>>

    @Query("SELECT * FROM thumbnails WHERE projectId = :projectId")
    suspend fun getThumbnailForProject(projectId: String): ThumbnailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThumbnail(thumb: ThumbnailEntity)

    @Query("DELETE FROM thumbnails WHERE id = :id")
    suspend fun deleteThumbnail(id: String)
}
