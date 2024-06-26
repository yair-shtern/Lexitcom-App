package lexitcom.lexitcomapp.data.room

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteColumn
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import lexitcom.lexitcomapp.data.models.Quiz
import lexitcom.lexitcomapp.data.models.RoomTypeConverters

@Database(
    entities = [Quiz::class],
    version = 2,
    autoMigrations = [
        AutoMigration(
            from = 1,
            to = 2,
            spec = QuizDatabase.MyAutoMigration::class
        )
    ],
    exportSchema = true
)
@TypeConverters(value = [RoomTypeConverters::class])
abstract class QuizDatabase : RoomDatabase() {

    abstract fun quizDao(): QuizDao

    companion object {
        @Volatile
        private var Instance: QuizDatabase? = null
        fun getDatabase(context: Context): QuizDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java,
                    "quiz_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

    @DeleteColumn(tableName = "quiz-table", columnName = "english")
    @DeleteColumn(tableName = "quiz-table", columnName = "translated")
    class MyAutoMigration : AutoMigrationSpec
}
