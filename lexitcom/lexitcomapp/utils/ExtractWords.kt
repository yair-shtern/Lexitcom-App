package lexitcom.lexitcomapp.utils//package lexitcom.lexitcomapp
//
//import java.io.File
//import java.nio.charset.Charset
//import com.github.pemistahl.lingua.api.Language
//import com.github.pemistahl.lingua.api.LanguageDetectorBuilder
//import com.github.pemistahl.lingua.api.LanguageDetector
//import com.github.pemistahl.lingua.api.MultiLanguageTranslatorBuilder
//import com.github.pemistahl.lingua.api.MultiLanguageTranslator
//
//val CURSE_WORDS = setOf(
//    "fuck", "shit", "damn", "bitch", "ass", "cock", "piss", "cunt", "twat", "bollocks",
//    "arse", "wanker", "hell", "fucking", "motherfucker", "son of a bitch", "piss off",
//    "screw you", "goddamn", "goddamnit", "holy shit", "jackass", "bastard", "crap",
//    "sod off", "bullshit", "knobhead", "bugger", "effing", "flipping", "frigging",
//    "sodding", "bloody", "crapola", "fubar", "fudge", "arsehole", "bollocking", "crapola",
//    "cock-up", "motherfrigger", "pissed off", "douchebag", "moron", "idiot", "loser",
//    "prick", "scumbag", "arsewipe", "dipshit", "dickhead", "asswipe", "cockwomble",
//    "bellend", "twatwaffle", "numpty", "wankstain", "shithead", "asshat", "cockroach",
//    "fuckwit", "shitforbrains", "jerkoff", "fucktard", "arsehat", "dumbass", "shitbag",
//    "sex", "sexy", "ass", "ass hole"
//)
//
//const val QUIZ_SIZE = 20
//
//enum class DifficultyLevels {
//    EASY, TOUGH, CHALLENGING
//}
//
//val EASY_THRESHOLD = 1000
//val TOUGH_THRESHOLD = 500
//
//fun classifyWordRarity(word: String, freqList: Map<String, Int>): DifficultyLevels {
//    val w = word.toLowerCase()
//    val wordFrequencyCount = freqList[w] ?: 0
//    return when {
//        wordFrequencyCount > EASY_THRESHOLD -> DifficultyLevels.EASY
//        wordFrequencyCount > TOUGH_THRESHOLD -> DifficultyLevels.TOUGH
//        else -> DifficultyLevels.CHALLENGING
//    }
//}
//
//class Word(val english: String, val translations: Map<Language, String>, val level: DifficultyLevels)
//
//fun processEpisode(filePath: String): Map<String, Int> {
//    val wordCount = mutableMapOf<String, Int>()
//    val rawLines = File(filePath).readLines()
//    val encoding = Charset.defaultCharset()
//    rawLines.forEach { line ->
//        val text = line.trim()
//        CURSE_WORDS.forEach { curseWord ->
//            text.replace(curseWord, "")
//        }
//        val words = text.split("\\s+".toRegex())
//            .filter { it.isNotBlank() }
//            .map { it.replace("""[^\w\s\']""".toRegex(), " ") }
//            .map { it.replace("\n", " ") }
//        words.forEach { word ->
//            if (!word.isNumeric() && word.length > 2) {
//                val formattedWord = word[0] + word.substring(1).replace("I", "l")
//                wordCount[formattedWord.toLowerCase()] = wordCount.getOrDefault(formattedWord.toLowerCase(), 0) + 1
//            }
//        }
//    }
//    return wordCount
//}
//
//fun createCsvFiles(seriesEpisodesFinalWords: Map<Int, Map<Int, Map<DifficultyLevels, List<Word>>>>, seriesName: String) {
//    seriesEpisodesFinalWords.forEach { (seasonNum, season) ->
//        season.forEach { (episodeNum, episodeWords) ->
//            val csvFile = "episode_$episodeNum.csv"
//            val csvPath = File("output_csv/$seriesName/Season $seasonNum/$csvFile").toPath()
//            csvPath.parent.toFile().mkdirs()
//            csvPath.toFile().bufferedWriter().use { writer ->
//                writer.write("English,${Language.values().joinToString(",") { it.toString() }},Level\n")
//                episodeWords.forEach { (level, words) ->
//                    words.forEach { word ->
//                        val translations = Language.values().associateWith { word.translations[it] ?: "" }
//                        val row = "${word.english},${translations.values.joinToString(",")},${level.name}\n"
//                        writer.write(row)
//                    }
//                }
//            }
//            println("CSV file '$csvFile' created successfully.")
//        }
//    }
//}
//
//fun divideWords(seriesEpisodes: Map<Int, Map<Int, List<String>>>, seriesFreq: MutableMap<String, Int>, translators: Map<Language, MultiLanguageTranslator>) {
//    val seriesWords = mutableMapOf<Int, MutableMap<Int, Map<DifficultyLevels, List<Word>>>>()
//    val chosenWords = mutableSetOf<String>()
//    seriesEpisodes.forEach { (seasonNum, season) ->
//        val seasonWords = mutableMapOf<Int, Map<DifficultyLevels, List<Word>>>()
//        season.forEach { (episodeNum, episodeWords) ->
//            val words = mutableMapOf<DifficultyLevels, MutableList<Word>>()
//            DifficultyLevels.values().forEach { words[it] = mutableListOf() }
//            while (words.values.sumBy { it.size } < QUIZ_SIZE * DifficultyLevels.values().size) {
//                val word = episodeWords.firstOrNull { it !in chosenWords } ?: episodeWords.first()
//                chosenWords.clear()
//                val wordLevel = classifyWordRarity(word, seriesFreq)
//                if (words[wordLevel]!!.size >= QUIZ_SIZE) continue
//                val translations = mutableMapOf<Language, String>()
//                translators.forEach { (lang, translator) ->
//                    try {
//                        translations[lang] = translator.translate(word)
//                    } catch (e: Exception) {
//                        println("Error translating '$word': ${e.message}")
//                    }
//                }
//                words[wordLevel]!!.add(Word(word, translations.toMap(), wordLevel))
//                chosenWords.add(word)
//            }
//            seasonWords[episodeNum] = words.mapValues { it.value.toList() }
//        }
//        seriesWords[seasonNum] = seasonWords
//    }
//}
//
//fun main() {
//    val inputDir = File("input_english_subtitles")
//    val outputDir = File("output_csv")
//    val translators = Language.values().associateWith {
//        MultiLanguageTranslatorBuilder.of(Language.ENGLISH, it).build()
//    }
//
//    inputDir.listFiles()?.forEach { seriesDir ->
//        if (seriesDir.isDirectory) {
//            val seriesName = seriesDir.name
//            val seriesFreq = mutableMapOf<String, Int>()
//            val seriesEpisodes = mutableMapOf<Int, Map<Int, List<String>>>()
//            seriesDir.listFiles()?.forEach { seasonDir ->
//                if (seasonDir.isDirectory) {
//                    val seasonNum = seasonDir.name.toIntOrNull() ?: return@forEach
//                    val seasonEpisodeWords = mutableMapOf<Int, List<String>>()
//                    seasonDir.listFiles()?.forEach { episodeFile ->
//                        if (episodeFile.isFile && episodeFile.extension == "srt") {
//                            val episodeWordCount = processEpisode(episodeFile.path)
//                            val sortedWords = episodeWordCount.toList().sortedByDescending { it.second }
//                            seasonEpisodeWords[episodeFile.nameWithoutExtension.toInt()] = sortedWords.map { it.first }
//                            sortedWords.forEach { (word, count) ->
//                                seriesFreq[word] = seriesFreq.getOrDefault(word, 0) + count
//                            }
//                        }
//                    }
//                    seriesEpisodes[seasonNum] = seasonEpisodeWords
//                }
//            }
//            val seriesEpisodesFinalWords = divideWords(seriesEpisodes, seriesFreq, translators)
//            createCsvFiles(seriesEpisodesFinalWords, seriesName)
//        }
//    }
//}
