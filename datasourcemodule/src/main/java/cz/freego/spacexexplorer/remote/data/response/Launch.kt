package cz.freego.spacexexplorer.remote.data.response

data class Launch(
    val links: Links?,
    val success: Boolean?,
    val flight_number: Int,
    val name: String,
    val date_unix: Long,
    val upcoming: Boolean,
    val id: String
)

data class Links(
    val patch: Patch?,
    val webcast: String?,
    val wikipedia: String?
)

data class Patch(
    val small: String?,
    val large: String?
)