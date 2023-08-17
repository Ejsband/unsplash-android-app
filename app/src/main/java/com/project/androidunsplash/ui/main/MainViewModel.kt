package com.project.androidunsplash.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.project.androidunsplash.domain.AccessTokenUseCase
import com.project.androidunsplash.domain.CachedImageUseCase
import com.project.androidunsplash.domain.MainCommonPagingSource
import com.project.androidunsplash.domain.MainSearchPagingSource
import com.project.androidunsplash.domain.MetaDataUseCase
import com.project.androidunsplash.domain.UserUseCase
import com.project.androidunsplash.entity.CachedImage
import com.project.androidunsplash.entity.MetaData
import com.project.androidunsplash.entity.UnsplashImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val accessTokenUseCase: AccessTokenUseCase,
    private val metaDataUseCase: MetaDataUseCase,
    private val cachedImageUseCase: CachedImageUseCase,
    private val userUseCase: UserUseCase
) : ViewModel() {

    val pagingRandomImageList = Pager(
        config = PagingConfig(pageSize = 20),
        pagingSourceFactory = { MainCommonPagingSource(userUseCase, metaDataUseCase) }
    ).flow.cachedIn(viewModelScope)

    lateinit var pagingSearchImageList: Flow<PagingData<UnsplashImage>>

    fun loadPagingSearchImageList(query: String) {
        pagingSearchImageList = Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { MainSearchPagingSource(userUseCase, metaDataUseCase, query) }
        ).flow.cachedIn(viewModelScope)
    }

    val cachedImage = cachedImageUseCase

    fun saveCachedImage(cachedImage: CachedImage) {
        viewModelScope.launch(Dispatchers.IO) {
            cachedImageUseCase.saveCachedImage(cachedImage)
        }
    }

    fun getCachedImageData() {
        viewModelScope.launch(Dispatchers.IO) {
            cachedImageUseCase.getCachedImageData()
        }
    }

    val metaData = metaDataUseCase

    private val _isTokenCorrect = MutableLiveData(false)
    val isTokenCorrect = _isTokenCorrect

    fun createMetaData(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        code: String,
        grantType: String
    ) {
        viewModelScope.launch {
            val token =
                accessTokenUseCase.getAccessToken(clientId, clientSecret, redirectUri, code, grantType)

            withContext(Dispatchers.IO) {
                val metaData = metaDataUseCase.getMetaData("key")

                if (metaData == null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        metaDataUseCase.saveMetaData(
                            MetaData(
                                "key",
                                token.accessToken,
                                token.tokenType,
                                token.refreshToken,
                                token.scope
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        _isTokenCorrect .value = true
                    }
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        metaDataUseCase.updateMetaData(
                            MetaData(
                                "key",
                                token.accessToken,
                                token.tokenType,
                                token.refreshToken,
                                token.scope
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        _isTokenCorrect .value = true
                    }
                }
            }
        }
    }

    fun checkIfMetaDataExists() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val metaData = metaDataUseCase.getMetaData("key")
                if (metaData == null) {
                    withContext(Dispatchers.Main) {
                        _isTokenCorrect.value = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        _isTokenCorrect.value = true
                    }
                }
            }
        }
    }
}