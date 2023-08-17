package com.project.androidunsplash.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.androidunsplash.entity.CachedImage
import com.project.androidunsplash.entity.MetaData

@Database(entities = [MetaData::class, CachedImage::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun metaDataDao(): MetaDataDao
    abstract fun cachedImageDao(): CachedImageDao
}