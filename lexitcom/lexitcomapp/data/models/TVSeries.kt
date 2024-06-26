package lexitcom.lexitcomapp.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TVSeries(
    val seriesId: Int,
    val name: String,
    val imageName: String,
    val quizImage: String,
    val seasons: MutableMap<Int, MutableMap<Int, TVEpisode>>,
    val numSeasons: Int = seasons.size,
    var position: SeriesPosition = SeriesPosition(1, 1)
) : Parcelable

//{
//    constructor(parcel: Parcel) : this(
//        parcel.readString() ?: "",
//        parcel.readString() ?: "",
//        parcel.readInt(),
//        mutableListOf<Season>().apply {
//            parcel.readList(this, Season::class.java.classLoader)
//        }
//    )
//
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeString(title)
//        parcel.writeString(description)
//        parcel.writeInt(id)
//        parcel.writeList(seasons)
//    }
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<TVSeries> {
//        override fun createFromParcel(parcel: Parcel): TVSeries {
//            return TVSeries(parcel)
//        }
//
//        override fun newArray(size: Int): Array<TVSeries?> {
//            return arrayOfNulls(size)
//        }
//    }
//}