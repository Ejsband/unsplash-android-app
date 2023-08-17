package com.project.androidunsplash.api

import com.project.androidunsplash.entity.Results
import com.project.androidunsplash.entity.UnsplashCollection
import com.project.androidunsplash.entity.UnsplashImage
import com.project.androidunsplash.entity.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

const val USER_URL = "https://api.unsplash.com"

interface UserApi {

    @GET("/me")
    suspend fun getUser(@Header("Authorization") accessToken: String): User

    @GET("/users/{userAlias}/likes")
    suspend fun getUserLikedImageList(
        @Path("userAlias") userAlias: String,
        @Header("Authorization") accessToken: String,
    ): Response<List<UnsplashImage>>

    @GET("/users/{userAlias}/collections")
    suspend fun getUserCollectionList(
        @Path("userAlias") userAlias: String,
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int
    ): Response<List<UnsplashCollection>>

    @GET("/collections/{collectionId}/photos")
    suspend fun getUserImageList(
        @Path("collectionId") collectionId: String,
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int
    ): Response<List<UnsplashImage>>

    @GET("/photos/{imageId}")
    suspend fun getUserImageItem(
        @Path("imageId") imageId: String,
        @Header("Authorization") accessToken: String
    ): UnsplashImage

    @GET("/photos")
    suspend fun getRandomImageList(
        @Header("Authorization") accessToken: String,
        @Query("page") page: Int
    ): Response<List<UnsplashImage>>

    @GET("/search/photos")
    suspend fun getSearchImageList(
        @Header("Authorization") accessToken: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<Results>

    @POST("/photos/{imageId}/like")
    suspend fun likeImage(
        @Path("imageId") imageId: String,
        @Header("Authorization") accessToken: String
    ): UnsplashImage

    @DELETE("/photos/{imageId}/like")
    suspend fun dislikeImage(
        @Path("imageId") imageId: String,
        @Header("Authorization") accessToken: String
    ): UnsplashImage


    companion object {
        fun create(): UserApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(USER_URL)
                .build()
            return retrofit.create(UserApi::class.java)
        }
    }
}