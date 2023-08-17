package com.project.androidunsplash.domain

import com.project.androidunsplash.entity.Token
import com.project.androidunsplash.repository.OauthRetrofitRepository
import javax.inject.Inject

class AccessTokenUseCase @Inject constructor(private val authRetrofitRepository: OauthRetrofitRepository) {

    suspend fun getAccessToken(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String,
        grantType: String
    ): Token {
        return authRetrofitRepository.getAccessToken(clientId, clientSecret, redirectUri, code, grantType)
    }
}