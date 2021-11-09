package cz.freego.spacexexplorer.remote.data.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "favorite_crews")
data class Crew (
    @PrimaryKey @Json(name = "id") var id: String,
    @ColumnInfo(name = "name") @Json(name = "name") var name: String,
    @ColumnInfo(name = "agency") @Json(name = "agency") var agency: String,
    @ColumnInfo(name = "image") @Json(name = "image") var image: String,
    @ColumnInfo(name = "wikipedia") @Json(name = "wikipedia") var wikipedia: String,
    @ColumnInfo(name = "status") @Json(name = "status") var status: String
)

