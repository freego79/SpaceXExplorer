package cz.freego.spacexexplorer.local

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cz.freego.spacexexplorer.remote.data.response.Crew
import kotlinx.coroutines.*

class LocalViewModel constructor(private val localRepository: LocalRepository) : ViewModel() {

    // local DB
    val favoriteCrewExists = MutableLiveData<Boolean>()
    val favoriteCrewList = MutableLiveData<List<Crew>>()

    var job: Job? = null

    fun addFavoriteCrew(crew: Crew) {
        job = CoroutineScope(Dispatchers.IO).launch {
            localRepository.addFavoriteCrew(crew)
        }
    }

    fun deleteFavoriteCrew(crew: Crew) {
        job = CoroutineScope(Dispatchers.IO).launch {
            localRepository.deleteFavoriteCrew(crew)
        }
    }

    fun favoriteCrewExists(id: String) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val favCrExists = localRepository.favoriteCrewExists(id)
            withContext(Dispatchers.Main) {
                favoriteCrewExists.postValue(favCrExists)
            }
        }
    }

    fun getAllFavoriteCrew() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val favCrList = localRepository.getAllFavoriteCrew()
            withContext(Dispatchers.Main) {
                favoriteCrewList.postValue(favCrList)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

}