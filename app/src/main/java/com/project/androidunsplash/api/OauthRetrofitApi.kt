package com.project.androidunsplash.api

import com.project.androidunsplash.entity.Token
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST
import retrofit2.http.Query

const val OAUTH_URL = "https://unsplash.com"

interface OauthApi {

    @POST("/oauth/token")
    suspend fun getToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("code") code: String,
        @Query("grant_type") grantType: String,
    ): Token

    companion object {
        fun create(): OauthApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(OAUTH_URL)
                .build()
            return retrofit.create(OauthApi::class.java)
        }
    }
}

