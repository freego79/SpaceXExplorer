package cz.freego.spacexexplorer.remote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RemoteViewModelFactory constructor(private val remoteRepository: RemoteRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(RemoteViewModel::class.java)) {
            RemoteViewModel(this.remoteRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}