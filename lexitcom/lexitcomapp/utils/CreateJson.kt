package lexitcom.lexitcomapp.utils

import lexitcom.lexitcomapp.data.models.SeriesPosition
import lexitcom.lexitcomapp.data.models.TVEpisode
import lexitcom.lexitcomapp.data.models.TVSeries
import java.io.File
import java.io.IOException

fun createSeasonMap(
    seriesName: String,
    seasonsList: List<Int>,
    quizImage: String
): MutableMap<Int, MutableMap<Int, TVEpisode>> {
    val seasons = mutableMapOf<Int, MutableMap<Int, TVEpisode>>()
    for ((seasonIndex, numEpisodes) in seasonsList.withIndex()) {
        val seasonMap = mutableMapOf<Int, TVEpisode>()
        for (episodeNum in 1..numEpisodes) {
            val episode = TVEpisode(
                seriesName = seriesName,
                seasonNum = seasonIndex + 1,
                episodeNum = episodeNum,
                imageName = quizImage,
                quizId = quizId
            )
            quizId++
            seasonMap[episodeNum] = episode
        }
        seasons[seasonIndex + 1] = seasonMap
    }
    return seasons
}

private var seriesId = 0
private var quizId = 0L

fun main() {
    val theOffice = createSeries(
        name = "The Office",
        imageName = "the_office.jpg",
        quizImage = "the_office.jpg",
        seasonsList = listOf(6, 22, 22, 14, 26, 26, 26, 24, 25)
    )

    val friends = createSeries(
        name = "Friends",
        imageName = "friends.jpg",
        quizImage = "friends.jpg",
        seasonsList = listOf(24, 24, 25, 24, 24, 25, 24, 24, 24, 18)
    )

    val seinfeld = createSeries(
        name = "Seinfeld",
        imageName = "seinfeld.jpg",
        quizImage = "seinfeld.jpg",
        seasonsList = listOf(5, 12, 22, 24, 22, 24, 24, 22, 24)
    )

    val bigBangTheory = createSeries(
        name = "The Big Bang Theory",
        imageName = "the_big_bang_theory.jpg",
        quizImage = "the_big_bang_theory.jpg",
        seasonsList = listOf(17, 17, 23, 24, 24, 24, 24, 24, 24, 24, 24, 24),
    )

    val brooklynNineNine = createSeries(
        name = "Brooklyn Nine-Nine",
        imageName = "brooklyn_99.jpg",
        quizImage = "brooklyn_99.jpg",
        seasonsList = listOf(22, 23, 23, 22, 22, 13, 13, 10)

    )

    val theSimpsons = createSeries(
        name = "The Simpsons",
        imageName = "the_simpsons.jpg",
        quizImage = "the_simpsons.jpg",
        seasonsList = listOf(
            13, 22, 24, 22, 22, 25, 25, 25, 25, 23, 22, 21, 22, 22, 22, 21, 22, 22, 20, 21,
            23, 22, 22, 22, 22, 22, 22, 22, 21, 23, 22, 22, 22
        )
    )

    val howIMetYourMother = createSeries(
        name = "How I Met Your Mother",
        imageName = "how_i_met_your_mother.jpg",
        quizImage = "how_i_met_your_mother.jpg",
        seasonsList = listOf(22, 22, 20, 24, 24, 24, 24, 24, 24)
    )

    val parksAndRecreation = createSeries(
        name = "Parks and Recreation",
        imageName = "parks_and_recreation.jpg",
        quizImage = "parks_and_recreation.jpg",
        seasonsList = listOf(6, 24, 16, 22, 22, 22, 13)
    )

    val theITCrowd = createSeries(
        name = "The IT Crowd",
        imageName = "the_it_crowd.jpg",
        quizImage = "the_it_crowd.jpg",
        seasonsList = listOf(6, 6, 6, 6)
    )


    val sitcoms = listOf(
        theOffice,
        friends,
        seinfeld,
        bigBangTheory,
        brooklynNineNine,
        theSimpsons,
        howIMetYourMother,
        parksAndRecreation,
        theITCrowd
    )

    val jsonString = sitcoms.toJson()

    try {
        val filePath = "app/src/main/assets/sitcoms.json"
        val file = File(filePath)
        file.createNewFile()
        file.writeText(jsonString)
        println("JSON data saved to '$filePath'")
    } catch (e: IOException) {
        println("Error creating or writing to 'sitcoms.json' file: ${e.message}")
    }
}

fun createSeries(
    name: String,
    imageName: String,
    quizImage: String,
    seasonsList: List<Int>,
    position: SeriesPosition = SeriesPosition(1, 1)
): TVSeries {
    val series = TVSeries(
        seriesId = seriesId,
        name = name,
        imageName = imageName,
        quizImage = quizImage,
        seasons = createSeasonMap(
            seriesName = name,
            quizImage = quizImage,
            seasonsList = seasonsList,
        ))
    seriesId++
    return series
}

fun List<TVSeries>.toJson(): String {
    return com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(this)
}