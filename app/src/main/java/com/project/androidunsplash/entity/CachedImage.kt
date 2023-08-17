package com.project.androidunsplash.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_image")
class CachedImage(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "source")
    val source: String,
    @ColumnInfo(name = "like_amount")
    val likes: Int,
    @ColumnInfo(name = "liked_by_user")
    val likedByUser: Boolean,
    @ColumnInfo(name = "user_name")
    val userName: String
)