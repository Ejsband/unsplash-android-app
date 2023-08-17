package com.project.androidunsplash.entity

import com.google.gson.annotations.SerializedName

class UnsplashImage(
    @SerializedName("id") val id: String,
    @SerializedName("urls") val url: ImageUrl,
    @SerializedName("likes") val likes: String,
    @SerializedName("liked_by_user") val likedByUser: Boolean,
    @SerializedName("user") val creator: User,
)

class ImageUrl(
    val regular: String
)

class Results(
    @SerializedName("results") val results: MutableList<UnsplashImage>
)