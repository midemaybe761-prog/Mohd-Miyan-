package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SceneDao {
    @Query("SELECT * FROM scenes WHERE projectId = :projectId ORDER BY sceneOrder ASC")
    fun getScenesForProject(projectId: String): Flow<List<SceneEntity>>

    @Query("SELECT * FROM scenes WHERE projectId = :projectId ORDER BY sceneOrder ASC")
    suspend fun getScenesList(projectId: String): List<SceneEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScenes(scenes: List<SceneEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)

    @Update
    suspend fun updateScene(scene: SceneEntity)

    @Query("DELETE FROM scenes WHERE id = :sceneId")
    suspend fun deleteScene(sceneId: String)

    @Query("DELETE FROM scenes WHERE projectId = :projectId")
    suspend fun deleteScenesForProject(projectId: String)
}
