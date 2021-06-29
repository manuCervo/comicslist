package mcervini.comicslist

import java.util.*

data class Series(val id: UUID, var name: String, val comics: MutableList<Comic> = mutableListOf())
