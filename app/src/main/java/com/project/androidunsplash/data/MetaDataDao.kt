package com.project.androidunsplash.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.project.androidunsplash.entity.MetaData

@Dao
interface MetaDataDao {

    @Query("SELECT * FROM meta_data WHERE id=:id")
    fun getMetaData(id: String): MetaData

    @Insert(entity = MetaData::class)
    fun saveMetaData(metaData: MetaData)

    @Update
    fun updateMetaData(metaData: MetaData)

    @Delete(entity = MetaData::class)
    fun deleteMetaData(metaData: MetaData)
}