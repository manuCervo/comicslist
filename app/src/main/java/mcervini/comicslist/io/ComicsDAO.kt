package mcervini.comicslist.io

import mcervini.comicslist.Availability
import mcervini.comicslist.Comic
import mcervini.comicslist.Series

/**
 * DAO for managing comics in a database
 */
interface ComicsDAO {

    /**
     * returns a list of all comics
     */
    fun getAllComics(): MutableList<Comic>

    /**
     * creates a new comic, inserts it into the database and returns it
     */
    fun createNewComic(
        series: Series,
        number: Int,
        title: String,
        availability: Availability
    ): Comic

    /**
     * updates all fields of a comic except the number
     * @param comic the comic with the updated fields
     */
    fun updateComic(comic: Comic)

    /**
     * updates the number of a comic
     * @param comic the comic to update
     * @param newNumber the new number of the comic
     */
    fun updateComicNumber(comic: Comic, newNumber: Int)

    /**
     * deletes a comic
     * @param comic the comic to delete
     */
    fun deleteComic(comic: Comic)


    /**
     * adds a previously created comic
     *
     * @param comic the comic to add
     */
    fun addExistingComic(comic: Comic)
}