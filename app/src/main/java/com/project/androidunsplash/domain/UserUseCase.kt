package com.project.androidunsplash.domain

import com.project.androidunsplash.entity.UnsplashCollection
import com.project.androidunsplash.entity.UnsplashImage
import com.project.androidunsplash.entity.User
import com.project.androidunsplash.repository.UserRetrofitRepository
import javax.inject.Inject

class UserUseCase @Inject constructor(private val userRetrofitRepository: UserRetrofitRepository) {
    suspend fun getUser(accessToken: String): User {
        return userRetrofitRepository.getUser(accessToken)
    }

    suspend fun getUserLikedImageList(userAlias: String, accessToken: String): List<UnsplashImage> {
        return userRetrofitRepository.getUserLikedImageList(userAlias, accessToken)
    }

    suspend fun getUserCollectionList(
        userAlias: String,
        accessToken: String,
        page: Int
    ): List<UnsplashCollection> {
        return userRetrofitRepository.getUserCollectionList(userAlias, accessToken, page)
    }

    suspend fun getUserImageList(collectionId: String, accessToken: String, page: Int): List<UnsplashImage> {
        return userRetrofitRepository.getUserImageList(collectionId, accessToken, page)
    }

    suspend fun getUserImageItem(imageId: String, accessToken: String): UnsplashImage {
        return userRetrofitRepository.getUserImageItem(imageId, accessToken)
    }

    suspend fun getRandomImageList(accessToken: String, page: Int): List<UnsplashImage> {
        return userRetrofitRepository.getRandomImageList(accessToken, page)
    }

    suspend fun getSearchImageList(
        accessToken: String,
        query: String,
        page: Int
    ): List<UnsplashImage> {
        return userRetrofitRepository.getSearchImageList(accessToken, query, page)
    }

    suspend fun likeImage(imageId: String, accessToken: String): UnsplashImage {
        return userRetrofitRepository.likeImage(imageId, accessToken)
    }

    suspend fun dislikeImage(imageId: String, accessToken: String): UnsplashImage {
        return userRetrofitRepository.dislikeImage(imageId, accessToken)
    }
}