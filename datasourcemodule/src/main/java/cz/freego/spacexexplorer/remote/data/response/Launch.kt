package cz.freego.spacexexplorer.remote.data.response

import com.squareup.moshi.Json

data class Launch(
    @Json(name = "links") val links: Links?,
    @Json(name = "success") val success: Boolean?,
    @Json(name = "flight_number") val flightNumber: Int,
    @Json(name = "name") val name: String,
    @Json(name = "date_unix") val dateUnix: Long,
    @Json(name = "upcoming") val upcoming: Boolean,
    @Json(name = "id") val id: String
) {
    data class Links(
        @Json(name = "patch") val patch: Patch?,
        @Json(name = "webcast") val webcast: String?,
        @Json(name = "wikipedia") val wikipedia: String?
    )

    data class Patch(
        @Json(name = "small") val small: String?,
        @Json(name = "large") val large: String?
    )
}

