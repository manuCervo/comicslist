package mcervini.comicslist

import java.util.*

data class Series(
    val id: UUID,
    var name: String,
    val comics: MutableList<Comic> = mutableListOf()
) {
    override fun toString(): String {
        return "Series(id=$id, name='$name', comicsCount=${comics.size})"
    }
}
