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
}