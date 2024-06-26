package lexitcom.lexitcomapp.data.room

import android.app.Application
import lexitcom.lexitcomapp.data.room.Graph

class QuizApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}