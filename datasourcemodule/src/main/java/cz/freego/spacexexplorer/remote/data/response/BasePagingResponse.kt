package cz.freego.spacexexplorer.remote.data.response

import com.squareup.moshi.Json

data class BasePagingResponse<T>(
    @Json(name = "docs") val items: List<T> = emptyList(),
    @Json(name = "totalPages") val total: Int = 0,
    @Json(name = "page") val page: Int
)
