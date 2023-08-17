package com.project.androidunsplash.domain

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.androidunsplash.entity.UnsplashCollection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FavouriteCollectionListPagingSource(
    private val userUseCase: UserUseCase,
    private val metaDataUseCase: MetaDataUseCase
) : PagingSource<Int, UnsplashCollection>() {

    override fun getRefreshKey(state: PagingState<Int, UnsplashCollection>): Int = PAGE_NUMBER

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashCollection> {
        return try {

            val data = withContext(Dispatchers.IO) {
                metaDataUseCase.getMetaData("key")
            }

            val token = "${data.tokenType} ${data.accessToken}"

            val user = withContext(Dispatchers.IO) {
                userUseCase.getUser(token)
            }

            val page = params.key ?: PAGE_NUMBER
            val response = userUseCase.getUserCollectionList(user.username, token, page)
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