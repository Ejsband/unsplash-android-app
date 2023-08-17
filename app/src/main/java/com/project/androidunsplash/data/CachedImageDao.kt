package com.project.androidunsplash.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.project.androidunsplash.entity.CachedImage

@Dao
interface CachedImageDao {

    @Query("SELECT * FROM cached_image")
    fun getCachedImageData(): List<CachedImage>

    @Insert(entity = CachedImage::class)
    fun saveCachedImage(cachedImage: CachedImage)

    @Query("DELETE FROM cached_image")
    fun deleteCachedImageData()
}