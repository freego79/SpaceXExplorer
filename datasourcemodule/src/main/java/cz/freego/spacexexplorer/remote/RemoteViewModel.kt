package cz.freego.spacexexplorer.remote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cz.freego.spacexexplorer.remote.data.datasource.CrewPagingSource
import cz.freego.spacexexplorer.remote.data.datasource.LaunchPagingSource
import cz.freego.spacexexplorer.remote.data.response.Crew
import cz.freego.spacexexplorer.remote.data.response.Launch
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class RemoteViewModel constructor(private val remoteRepository: RemoteRepository) : ViewModel() {

    // remote API
    val errorMessage = MutableLiveData<String>()
    val latestLaunch = MutableLiveData<Launch>()
    val upcomingLaunches = MutableLiveData<List<Launch>>()

    var job: Job? = null
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    val loading = MutableLiveData<Boolean>()

    fun getCrews(text: String): Flow<PagingData<Crew>> {
        val listData = Pager(PagingConfig(pageSize = ApiService.PAGE_LIMIT)) {
            CrewPagingSource(remoteRepository.apiService, text)
        }.flow.cachedIn(viewModelScope)
        return listData
    }

    fun getLaunches(upcoming: Boolean, asc: Boolean): Flow<PagingData<Launch>> {
        val listData = Pager(PagingConfig(pageSize = ApiService.PAGE_LIMIT)) {
            LaunchPagingSource(remoteRepository.apiService, upcoming, asc)
        }.flow.cachedIn(viewModelScope)
        return listData
    }

    fun getLatestLaunch() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = remoteRepository.getLatestLauch()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    latestLaunch.postValue(response.body())
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun getUpcomingLaunches() {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = remoteRepository.getUpcomingLauches()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    upcomingLaunches.postValue(response.body())
                    loading.postValue(false)
                } else {
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.postValue(message)
        loading.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}