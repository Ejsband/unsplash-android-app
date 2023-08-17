package com.project.androidunsplash.di

import android.app.Application
import androidx.room.Room
import com.project.androidunsplash.data.AppDatabase
import com.project.androidunsplash.data.CachedImageDao
import com.project.androidunsplash.data.MetaDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, "unsplash_data")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMetaDataDao(db: AppDatabase): MetaDataDao {
        return db.metaDataDao()
    }

    @Provides
    fun provideCachedImageDao(db: AppDatabase): CachedImageDao {
        return db.cachedImageDao()
    }
}