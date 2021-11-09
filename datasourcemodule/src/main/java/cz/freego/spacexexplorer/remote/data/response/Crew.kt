package cz.freego.spacexexplorer.remote.data.response

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_crews")
data class Crew (
    @PrimaryKey var id: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "agency") var agency: String,
    @ColumnInfo(name = "image") var image: String,
    @ColumnInfo(name = "wikipedia") var wikipedia: String,
    @ColumnInfo(name = "status") var status: String
)

