package cz.freego.spacexexplorer.remote.data.request

import com.squareup.moshi.Json

data class CrewQueryModel(
    @Json(name = "query") val query: Query,
    @Json(name = "options") val options: Options
) {
    constructor(search: String, page: Int, limit: Int): this(Query(Name(search)), Options(page, limit))

    data class Query(
        @Json(name = "name") val name: Name
    )

    data class Name(
        @Json(name = "\$regex") val reqex: String,
        @Json(name = "\$options") val options: String = "i"
    )

    data class Options(
        @Json(name = "page") val page: Int,
        @Json(name = "limit") val limit: Int,
        @Json(name = "pagination") val pagination: Boolean = true
    )

}

