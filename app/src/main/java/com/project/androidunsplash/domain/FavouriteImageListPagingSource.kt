package com.project.androidunsplash.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.androidunsplash.entity.UnsplashImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavouriteImageListPagingSource (
    private val userUseCase: UserUseCase,
    private val metaDataUseCase: MetaDataUseCase,
    private val collectionId: String
) : PagingSource<Int, UnsplashImage>() {

    override fun getRefreshKey(state: PagingState<Int, UnsplashImage>): Int = PAGE_NUMBER

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashImage> {
        return try {

            val data = withContext(Dispatchers.IO) {
                metaDataUseCase.getMetaData("key")
            }

            val token = "${data.tokenType} ${data.accessToken}"

            val page = params.key ?: PAGE_NUMBER
            val response = userUseCase.getUserImageList(collectionId, token, page)
            LoadResult.Page(
                data = response,
                prevKey = null,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    private companion object {
        private const val PAGE_NUMBER = 1
    }
}