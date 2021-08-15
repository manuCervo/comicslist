package mcervini.comicslist.io

import mcervini.comicslist.Availability
import mcervini.comicslist.Comic
import mcervini.comicslist.Series

interface ComicsDAO {
    fun getAllComics(): MutableList<Comic>
    fun createNewComic(
        series: Series,
        number: Int,
        title: String,
        availability: Availability
    ): Comic

    fun updateComic(comic: Comic)
    fun deleteComic(comic: Comic)
    fun updateComicNumber(comic: Comic, newNumber: Int)
    fun addExistingComic(comic: Comic)
}