package com.project.androidunsplash.repository

import com.project.androidunsplash.api.OauthApi
import com.project.androidunsplash.entity.Token
import javax.inject.Inject

class OauthRetrofitRepository  @Inject constructor() {

    private val oauthApi = OauthApi.create()

    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String,
        grantType: String
    ): Token {
        return oauthApi.getToken(clientId, clientSecret, redirectUri, code, grantType)
    }
}