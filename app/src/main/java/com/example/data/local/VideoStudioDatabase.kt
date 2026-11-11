package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProjectEntity::class, SceneEntity::class, ThumbnailEntity::class],
    version = 1,
    exportSchema = false
)
abstract class VideoStudioDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun sceneDao(): SceneDao
    abstract fun thumbnailDao(): ThumbnailDao

    companion object {
        @Volatile
        private var INSTANCE: VideoStudioDatabase? = null

        fun getDatabase(context: Context): VideoStudioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideoStudioDatabase::class.java,
                    "video_studio_database.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
