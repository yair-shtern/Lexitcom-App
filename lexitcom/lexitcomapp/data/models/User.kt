package lexitcom.lexitcomapp.data.models

data class User(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val userLanguage: String = "",
    val favorites: MutableSet<Int> = mutableSetOf(),
    val progress: MutableMap<Int, SeriesPosition> = mutableMapOf()
)
