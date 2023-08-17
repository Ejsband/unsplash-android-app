package com.project.androidunsplash.domain

import com.project.androidunsplash.entity.CachedImage
import com.project.androidunsplash.repository.RoomRepository
import javax.inject.Inject

class CachedImageUseCase @Inject constructor(private val roomRepository: RoomRepository) {

    fun saveCachedImage(cachedImage: CachedImage) {
        roomRepository.getCachedImageDao().saveCachedImage(cachedImage)
    }

    fun getCachedImageData(): List<CachedImage> {
        return roomRepository.getCachedImageDao().getCachedImageData()
    }

    fun deleteCachedImageData() {
        return roomRepository.getCachedImageDao().deleteCachedImageData()
    }
}