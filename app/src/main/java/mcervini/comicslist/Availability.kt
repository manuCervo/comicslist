package mcervini.comicslist

/**
 * represents the availability of a comic
 *
 * @property value a value used for storing an availability object
 * @property colorRes the resource of the color associated with an availability
 * @property stringRes the resource of the text to display
 */
enum class Availability(val value: Int, val colorRes: Int, val stringRes: Int) {
    AVAILABLE(0, R.color.comic_available, R.string.available),
    NOT_AVAILABLE(1, R.color.comic_missing, R.string.not_available),
    BOOKED(2, R.color.comic_booked, R.string.booked);

    companion object {
        /**
         * returns the corresponding availability for the given value
         */
        fun fromValue(value: Int): Availability {
            return when (value) {
                0 -> AVAILABLE
                1 -> NOT_AVAILABLE
                2 -> BOOKED
                else -> throw IllegalArgumentException()
            }
        }
    }
}