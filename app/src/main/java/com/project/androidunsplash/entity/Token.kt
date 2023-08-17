package com.project.androidunsplash.entity

import com.google.gson.annotations.SerializedName

class Token(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("scope") val scope: String
    )