package mcervini.comicslist

import java.util.*

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
