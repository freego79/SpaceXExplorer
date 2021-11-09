package cz.freego.spacexexplorer.remote.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cz.freego.spacexexplorer.remote.ApiService
import cz.freego.spacexexplorer.remote.data.request.CrewQueryModel
import cz.freego.spacexexplorer.remote.data.response.Crew

class CrewPagingSource(private val apiService: ApiService, private val search: String) : PagingSource<Int, Crew>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Crew> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val response = apiService.getCrews(
                CrewQueryModel(search, currentLoadingPageKey, ApiService.PAGE_LIMIT))
            val responseData = mutableListOf<Crew>()
            val data = response.body()?.items ?: emptyList()
            responseData.addAll(data)

            val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1
            val nextKey = if (
                currentLoadingPageKey.plus(1) > response.body()!!.total) null
            else currentLoadingPageKey.plus(1)

            return LoadResult.Page(
                data = responseData,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Crew>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

}