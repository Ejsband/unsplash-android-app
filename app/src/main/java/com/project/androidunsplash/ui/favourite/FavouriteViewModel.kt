package com.project.androidunsplash.ui.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.project.androidunsplash.domain.FavouriteCollectionListPagingSource
import com.project.androidunsplash.domain.FavouriteImageListPagingSource
import com.project.androidunsplash.domain.MetaDataUseCase
import com.project.androidunsplash.domain.UserUseCase
import com.project.androidunsplash.entity.Image
import com.project.androidunsplash.entity.ImageUrl
import com.project.androidunsplash.entity.UnsplashImage
import com.project.androidunsplash.entity.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val metaDataUseCase: MetaDataUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {

    val pagingCollectionList = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { FavouriteCollectionListPagingSource(userUseCase, metaDataUseCase) }
    ).flow.cachedIn(viewModelScope)

    lateinit var pagingImageList: Flow<PagingData<UnsplashImage>>

    fun loadPagingImageList(collectionId: String) {
        pagingImageList = Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = {
                FavouriteImageListPagingSource(
                    userUseCase,
                    metaDataUseCase,
                    collectionId
                )
            }
        ).flow.cachedIn(viewModelScope)
    }

    private val _imageItemState = MutableStateFlow(
        UnsplashImage(
            "",
            ImageUrl(""),
            "",
            false,
            User("", "", "", Image(""))
        )
    )
    val imageItemState = _imageItemState.asStateFlow()

    fun reloadUserImageItem(imageId: String) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                metaDataUseCase.getMetaData("key")
            }
            val token = "${data.tokenType} ${data.accessToken}"
            _imageItemState.value = userUseCase.getUserImageItem(imageId, token)
        }
    }

    fun likeImage(imageId: String) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                metaDataUseCase.getMetaData("key")
            }
            userUseCase.likeImage(imageId, "${data.tokenType} ${data.accessToken}")
        }
    }

    fun dislikeImage(imageId: String) {
        viewModelScope.launch {
            val data = withContext(Dispatchers.IO) {
                metaDataUseCase.getMetaData("key")
            }
            userUseCase.dislikeImage(imageId, "${data.tokenType} ${data.accessToken}")
        }
    }
}