package cz.freego.spacexexplorer.local

import cz.freego.spacexexplorer.local.db.AppDatabase
import cz.freego.spacexexplorer.remote.data.response.Crew

class LocalRepository constructor(private val appDatabase: AppDatabase) {

    // local DB
    suspend fun addFavoriteCrew(crew: Crew) = appDatabase.FavoriteCrewDao().insert(crew)

    suspend fun deleteFavoriteCrew(crew: Crew) = appDatabase.FavoriteCrewDao().delete(crew)

    suspend fun favoriteCrewExists(id: String) = appDatabase.FavoriteCrewDao().favoriteCrewExists(id)

    suspend fun getAllFavoriteCrew() = appDatabase.FavoriteCrewDao().getAllFavoriteCrew()

}