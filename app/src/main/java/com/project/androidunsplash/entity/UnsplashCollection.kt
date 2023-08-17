package com.project.androidunsplash.entity

import com.google.gson.annotations.SerializedName

class UnsplashCollection(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("total_photos") val totalPhotos: String,
    @SerializedName("cover_photo") val coverPhoto: CoverPhoto
    )

class CoverPhoto(
    @SerializedName("urls") val url: CollectionUrl
)

class CollectionUrl(
    @SerializedName("regular") val regular: String
)