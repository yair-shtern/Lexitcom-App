//package lexitcom.lexitcomapp.subtitles
//
//import android.app.Application
//import android.app.LocaleConfig
//import android.content.Context
//import java.io.FileInputStream
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import retrofit2.http.GET
//import retrofit2.http.Query
//import java.io.BufferedReader
//import java.io.InputStreamReader
//import java.net.HttpURLConnection
//import kotlinx.coroutines.Dispatchers
//import java.io.File
//import java.io.FileOutputStream
//import java.net.URL
//import android.content.res.AssetManager
//import android.content.res.Resources
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.platform.LocalContext
//
//interface OpenSubtitlesApi {
//    @GET("search/query")
//    suspend fun searchSubtitles(
//        @Query("query") query: String,
//        @Query("season") season: Int? = null,
//        @Query("episode") episode: Int? = null
//    ): SearchResponse
//
//    data class SearchResponse(
//        val data: List<SubtitleInfo>
//    )
//
//    data class SubtitleInfo(
//        val idSubtitleFile: String,
//        val movieName: String,
//        val movieReleaseName: String,
//        val subtitleDownloadsLink: String
//    )
//}
//
//class OpenSubtitlesLoader {
//    private val api: OpenSubtitlesApi
//
//    init {
//        val retrofit = Retrofit.Builder()
//            .baseUrl("https://rest.opensubtitles.org")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        api = retrofit.create(OpenSubtitlesApi::class.java)
//    }
//
//    suspend fun loadSubtitles(
//        seriesName: String,
//        season: Int? = null,
//        episode: Int? = null
//    ): List<OpenSubtitlesApi.SubtitleInfo> {
//        val response = api.searchSubtitles(seriesName, season, episode)
//        return response.data
//    }
//}
//
//class SubtitleRepository(private val openSubtitlesLoader: OpenSubtitlesLoader) {
//
//    suspend fun getSubtitles(
//        seriesName: String,
//        season: Int? = null,
//        episode: Int? = null
//    ): List<OpenSubtitlesApi.SubtitleInfo> {
//        return openSubtitlesLoader.loadSubtitles(seriesName, season, episode)
//    }
//}
//
//
//class MyViewModel(private val subtitleRepository: SubtitleRepository, private val context: Context) : ViewModel() {
//
//    fun loadSubtitles(seriesName: String, season: Int? = null, episode: Int? = null) {
//        viewModelScope.launch {
//            val subtitles = subtitleRepository.getSubtitles(seriesName, season, episode)
//            subtitles.forEach { subtitleInfo ->
//                val fileName = "${subtitleInfo.movieName}.srt"
//                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fileOutputStream ->
//                    val subtitleContent = downloadSubtitleContent(subtitleInfo.subtitleDownloadsLink)
//                    fileOutputStream.write(subtitleContent.toByteArray())
//                }
//            }
//        }
//    }
//
//    private suspend fun downloadSubtitleContent(url: String): String {
//        return withContext(Dispatchers.IO) {
//            var connection:HttpURLConnection? = null
//            try {
//                connection = URL(url).openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//                connection.doInput = true
//
//                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
//                    val inputStream = connection.inputStream
//                    val reader = BufferedReader(InputStreamReader(inputStream))
//                    val content = reader.readText()
//                    reader.close()
//                    content
//                } else {
//                    ""
//                }
//            } catch (e: Exception) {
//                ""
//            } finally {
//                connection?.disconnect()
//            }
//        }
//    }
//}
//
//@Composable
//fun titles(){
//    val testContext = LocalContext.current
//    val subtitleRepository = SubtitleRepository(OpenSubtitlesLoader())
//    val viewModel = MyViewModel(subtitleRepository, testContext)
//
//    viewModel.loadSubtitles("Game of Thrones", season = 1, episode = 1)
//}
