package cz.freego.spacexexplorer.local.db

import androidx.room.*
import cz.freego.spacexexplorer.remote.data.response.Crew

@Dao
interface FavoriteCrewDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crew: Crew)

    @Delete
    suspend fun delete(crew: Crew)

    @Query("SELECT EXISTS(SELECT * FROM favorite_crews WHERE id = :id)")
    suspend fun favoriteCrewExists(id : String) : Boolean

    @Query("SELECT * FROM favorite_crews")
    suspend fun getAllFavoriteCrew(): List<Crew>

    @Query("SELECT * FROM favorite_crews WHERE id = :id")
    suspend fun findById(id: String): Crew?

    //TODO TESTING

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTest(crew: Crew)

    @Query("SELECT * FROM favorite_crews WHERE id = :id")
    fun findByIdTest(id: String): Crew?

}