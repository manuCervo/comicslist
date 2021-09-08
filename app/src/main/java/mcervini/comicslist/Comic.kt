package mcervini.comicslist

/**
 * class for representing a comic
 *
 * @param series the series where this comics belongs
 * @param number the number of this comic
 * @param title the title of this comic (can be empty)
 * @param availability the availability of this comic
 */
data class Comic(
    val series: Series,
    var number: Int,
    var title: String,
    var availability: Availability
) : Comparable<Comic> {


    override fun toString(): String {
        return "Comic(series=${series}, number=$number, title='$title', availability=$availability)"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is Comic) {
            return number == other.number && title == other.title && availability == other.availability && series.id == other.series.id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        var result = series.hashCode()
        result = 31 * result + number
        result = 31 * result + title.hashCode()
        result = 31 * result + availability.hashCode()
        return result
    }

    override fun compareTo(other: Comic): Int {
        return number.compareTo(other.number)
    }
}