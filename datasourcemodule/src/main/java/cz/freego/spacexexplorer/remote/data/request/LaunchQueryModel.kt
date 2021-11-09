package cz.freego.spacexexplorer.remote.data.request

import com.squareup.moshi.Json

data class LaunchQueryModel(
    @Json(name = "query") val query: Query,
    @Json(name = "options") val options: Options
) {
    constructor(upcoming: Boolean, page: Int, limit: Int, asc: Boolean):
            this(Query(upcoming), Options(page, limit, Sort(if (asc) "asc" else "desc")))

    data class Query(
        @Json(name = "upcoming") val upcoming: Boolean
    )

    data class Options(
        @Json(name = "page") val page: Int,
        @Json(name = "limit") val limit: Int,
        @Json(name = "sort") val sort: Sort,
        @Json(name = "pagination") val pagination: Boolean = true
    )

    data class Sort(
        @Json(name = "date_unix") val dateUnix: String
    )

}

