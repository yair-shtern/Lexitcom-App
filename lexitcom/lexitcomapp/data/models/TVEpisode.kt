package lexitcom.lexitcomapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TVEpisode(
    val seriesName: String,
    val seasonNum: Int,
    val episodeNum: Int,
    val imageName: String,
    val quizId: Long
):Parcelable
