package lexitcom.lexitcomapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeriesPosition(var seasonNum: Int, var episodeNum: Int) : Parcelable
