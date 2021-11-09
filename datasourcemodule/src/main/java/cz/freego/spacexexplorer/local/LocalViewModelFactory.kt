package cz.freego.spacexexplorer.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LocalViewModelFactory constructor(private val localRepository: LocalRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(LocalViewModel::class.java)) {
            LocalViewModel(this.localRepository) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}