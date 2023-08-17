package com.project.androidunsplash.domain

import com.project.androidunsplash.entity.MetaData
import com.project.androidunsplash.repository.RoomRepository
import javax.inject.Inject

class MetaDataUseCase @Inject constructor(private val roomRepository: RoomRepository) {

    fun getMetaData(id: String): MetaData {
        return roomRepository.getMetaDataDao().getMetaData(id)
    }

    fun saveMetaData(metaData: MetaData) {
        roomRepository.getMetaDataDao().saveMetaData(metaData)
    }

    fun updateMetaData(metaData: MetaData) {
        roomRepository.getMetaDataDao().updateMetaData(metaData)
    }

    fun deleteMetaData(metaData: MetaData) {
        roomRepository.getMetaDataDao().deleteMetaData(metaData)
    }
}