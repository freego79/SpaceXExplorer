package cz.freego.spacexexplorer.remote

import android.content.Context
import cz.freego.spacexexplorer.BuildConfig
import cz.freego.spacexexplorer.Utils
import cz.freego.spacexexplorer.remote.data.response.BasePagingResponse
import cz.freego.spacexexplorer.remote.data.response.Crew
import cz.freego.spacexexplorer.remote.data.request.CrewQueryModel
import cz.freego.spacexexplorer.remote.data.request.LaunchQueryModel
import cz.freego.spacexexplorer.remote.data.response.Launch
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("crew/query")
    suspend fun getCrews(@Body query: CrewQueryModel) : Response<BasePagingResponse<Crew>>

    @POST("launches/query")
    suspend fun getLaunches(@Body query: LaunchQueryModel) : Response<BasePagingResponse<Launch>>

    @GET("launches/latest")
    suspend fun getLatestLauch() : Response<Launch>

    @GET("launches/upcoming")
    suspend fun getUpcomingLauches() : Response<List<Launch>>

    companion object {

        const val PAGE_LIMIT = BuildConfig.PAGE_LIMIT
        private const val BASE_URL = BuildConfig.API_BASE_URL

        var apiService: ApiService? = null
        fun getInstance(context: Context) : ApiService {
            if (apiService == null) {
                val cacheSize = (5 * 1024 * 1024).toLong()
                val myCache = Cache(context.cacheDir, cacheSize)
                val logger = HttpLoggingInterceptor()
                logger.level = Level.BASIC

                val okHttpClient = OkHttpClient.Builder()
                    .cache(myCache)
                    .addInterceptor(logger)
                    .addInterceptor { chain ->
                        var request = chain.request()
                        request = if (Utils.hasNetwork(context)!!)
                            request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                        else
                            request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                        chain.proceed(request)
                    }
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create(Utils.getMoshi()))
                    .client(okHttpClient)
                    .build()
                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService!!
        }

    }
}