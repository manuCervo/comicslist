package mcervini.comicslist


enum class Availability(val value: Int, val colorRes: Int, val stringRes: Int) {
    AVAILABLE(0, R.color.comic_available, R.string.available),
    NOT_AVAILABLE(1, R.color.comic_missing, R.string.not_available),
    BOOKED(2, R.color.comic_booked, R.string.booked)
}