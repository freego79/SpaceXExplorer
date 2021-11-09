package cz.freego.spacexexplorer.remote.data.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cz.freego.spacexexplorer.remote.ApiService
import cz.freego.spacexexplorer.remote.data.request.CrewQueryModel
import cz.freego.spacexexplorer.remote.data.request.LaunchQueryModel
import cz.freego.spacexexplorer.remote.data.response.Crew
import cz.freego.spacexexplorer.remote.data.response.Launch

class LaunchPagingSource(private val apiService: ApiService,
                         private val upcoming: Boolean, private val asc: Boolean) : PagingSource<Int, Launch>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Launch> {
        try {
            val currentLoadingPageKey = params.key ?: 1
            val response = apiService.getLaunches(
                LaunchQueryModel(upcoming, currentLoadingPageKey, ApiService.PAGE_LIMIT, asc)
            )
            val responseData = mutableListOf<Launch>()
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

    override fun getRefreshKey(state: PagingState<Int, Launch>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

}