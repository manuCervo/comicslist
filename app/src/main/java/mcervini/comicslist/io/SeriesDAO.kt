package mcervini.comicslist.io

import mcervini.comicslist.Availability
import mcervini.comicslist.Series

/**
 * DAO for managing series in a database
 */
interface SeriesDAO {
    /**
     * returns a list containing all the series in the database
     */
    fun getAllSeries(): MutableList<Series>

    /**
     * creates a new series and returns it
     */
    fun createNewSeries(name: String, numberOfComics: Int, availability: Availability): Series

    /**
     * updates a series
     */
    fun updateSeries(series: Series)

    /**
     * deletes a series
     */
    fun deleteSeries(series: Series)

    /**
     * adds a series that was previously created
     */
    fun addExistingSeries(series: Series)
}