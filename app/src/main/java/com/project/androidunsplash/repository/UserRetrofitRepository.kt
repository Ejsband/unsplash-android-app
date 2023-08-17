package com.project.androidunsplash.repository

import com.project.androidunsplash.api.UserApi
import com.project.androidunsplash.entity.UnsplashCollection
import com.project.androidunsplash.entity.UnsplashImage
import com.project.androidunsplash.entity.User
import javax.inject.Inject

class UserRetrofitRepository @Inject constructor() {

    private val userApi = UserApi.create()

    suspend fun getUser(accessToken: String): User {
        return userApi.getUser(accessToken)
    }

    suspend fun getUserLikedImageList(userAlias: String, accessToken: String): List<UnsplashImage> {
        val api = userApi.getUserLikedImageList(userAlias, accessToken)
        return api.body() ?: emptyList()
    }

    suspend fun getUserCollectionList(
        userAlias: String,
        accessToken: String,
        page: Int
    ): List<UnsplashCollection> {
        val api = userApi.getUserCollectionList(userAlias, accessToken, page)
        return api.body() ?: emptyList()
    }

    suspend fun getUserImageList(collectionId: String, accessToken: String, page: Int): List<UnsplashImage> {
        val api = userApi.getUserImageList(collectionId, accessToken, page)
        return api.body() ?: emptyList()
    }

    suspend fun getUserImageItem(imageId: String, accessToken: String): UnsplashImage {
        return userApi.getUserImageItem(imageId, accessToken)
    }

    suspend fun getRandomImageList(accessToken: String, page: Int): List<UnsplashImage> {
        val api = userApi.getRandomImageList(accessToken, page)
        return api.body() ?: emptyList()
    }

    suspend fun getSearchImageList(
        accessToken: String,
        query: String,
        page: Int
    ): List<UnsplashImage> {
        val api = userApi.getSearchImageList(accessToken, query, page)
        return api.body()?.results ?: emptyList()
    }

    suspend fun likeImage(imageId: String, accessToken: String): UnsplashImage {
        return userApi.likeImage(imageId, accessToken)
    }

    suspend fun dislikeImage(imageId: String, accessToken: String): UnsplashImage {
        return userApi.dislikeImage(imageId, accessToken)
    }
}