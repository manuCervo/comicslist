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

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Series) {
            return other.id.equals(this.id) && other.name.equals(this.name)
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + comics.hashCode()
        return result
    }
}
