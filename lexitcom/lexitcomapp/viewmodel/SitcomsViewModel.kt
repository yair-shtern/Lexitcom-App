package lexitcom.lexitcomapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import lexitcom.lexitcomapp.data.models.SeriesPosition
import lexitcom.lexitcomapp.data.models.TVEpisode
import lexitcom.lexitcomapp.data.models.TVSeries


class SitcomsViewModel(context: Context) : ViewModel() {
    private val jsonString =
        context.assets.open("sitcoms.json").bufferedReader().use { it.readText() }
    private val _sitcoms: MutableList<TVSeries> =
        Gson().fromJson(jsonString, object : TypeToken<MutableList<TVSeries>>() {}.type)
    private val sitcoms: MutableList<TVSeries> = _sitcoms


    // Function to return all TV series
    fun getAll(): MutableList<TVSeries> {
        return sitcoms
    }

    // Function to get TV series by its ID
    fun getSitcomById(id: Int): TVSeries? {
        return sitcoms.firstOrNull { it.seriesId == id }
    }

    // Function to update position of a TV series
    fun updatePosition(seriesId: Int, seasonNum: Int, episodeNum: Int) {
        getSitcomById(seriesId)?.position = SeriesPosition(seasonNum, episodeNum)
    }

    // Function to get an episode by its IDs
    fun getEpisodeByIdAndPosition(seriesId: Int, seasonNum: Int, episodeNum: Int): TVEpisode? {
        return getSitcomById(seriesId)?.seasons?.get(seasonNum)?.get(episodeNum)
    }

    // Function to get the previous episode
    fun getPrevEpisode(seriesId: Int, seasonNum: Int, episodeNum: Int): TVEpisode? {
        if (seasonNum == 1 && episodeNum == 1) return null
        if (episodeNum == 1) {
            val season = getSitcomById(seriesId)?.seasons?.get(seasonNum - 1)
            return season?.get(season.size)
        }
        return getEpisodeByIdAndPosition(seriesId, seasonNum, episodeNum - 1)
    }

    // Function to get the next episode
    fun getNextEpisode(seriesId: Int, seasonNum: Int, episodeNum: Int): TVEpisode? {
        val series = getSitcomById(seriesId)
        if (series != null) {
            if (seasonNum == series.seasons.size && episodeNum == series.seasons[seasonNum]!!.size) {
                return null
            }

            if (episodeNum == series.seasons[seasonNum]!!.size) {
                return series.seasons[seasonNum + 1]?.get(1)
            }
            return getEpisodeByIdAndPosition(seriesId, seasonNum, episodeNum + 1)
        }
        return null
    }
}
