package cz.freego.spacexexplorer.remote

class RemoteRepository constructor(val apiService: ApiService) {

    suspend fun getLatestLauch() = apiService.getLatestLauch()

    suspend fun getUpcomingLauches() = apiService.getUpcomingLauches()

}