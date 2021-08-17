package mcervini.comicslist


data class Comic(
    val series: Series,
    var number: Int,
    var title: String,
    var availability: Availability
) {


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
}