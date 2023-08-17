package com.project.androidunsplash.repository

import com.project.androidunsplash.data.AppDatabase
import com.project.androidunsplash.data.CachedImageDao
import com.project.androidunsplash.data.MetaDataDao
import javax.inject.Inject

class RoomRepository @Inject constructor(private val db: AppDatabase) {
    fun getMetaDataDao(): MetaDataDao {
        return db.metaDataDao()
    }

    fun getCachedImageDao(): CachedImageDao {
        return db.cachedImageDao()
    }
}