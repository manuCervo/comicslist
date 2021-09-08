package mcervini.comicslist

import java.util.*

/**
 * class for representing a series. A series is a collection of comics
 * @param id the unique id of the series
 * @param name the name of the series
 * @param comics the list of comics of this series
 */
data class Series(
    val id: UUID,
    var name: String,
    val comics: MutableList<Comic> = mutableListOf()
) : Comparable<Series> {
    override fun toString(): String {
        return "Series(id=$id, name='$name', comicsCount=${comics.size})"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Series) {
            return other.id == this.id && other.name == this.name
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }


    override fun compareTo(other: Series): Int {
        return name.compareTo(other.name)
    }
}
