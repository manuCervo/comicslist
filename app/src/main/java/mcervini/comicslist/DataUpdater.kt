package mcervini.comicslist

import androidx.recyclerview.widget.SortedList
import mcervini.comicslist.io.ComicsDAO
import mcervini.comicslist.io.SeriesDAO

/**
 * updates the comics and the series in the database and reflects the changes in the lists
 */
class DataUpdater(
    private val seriesDAO: SeriesDAO,
    private val comicsDAO: ComicsDAO,
    private val list: MutableList<Series>,
    private val displayingList: SortedList<Series>
) {
    /**
     * creates a series and returns it
     */
    fun createSeries(name: String, numberOfComics: Int, availability: Availability): Series {
        val series: Series = seriesDAO.createNewSeries(name, numberOfComics, availability)
        list.add(series)
        displayingList.add(series)
        return series
    }

    /**
     * deletes a series
     */
    fun deleteSeries(series: Series) {
        seriesDAO.deleteSeries(series)
        list.remove(series)
        displayingList.remove(series)
    }

    /**
     * updates a series
     */
    fun updateSeries(series: Series) {
        seriesDAO.updateSeries(series)
        updateDisplayedSeries(series)
    }

    /**
     * creates a comic and returns it
     */
    fun createComic(series: Series, number: Int, title: String, availability: Availability): Comic {
        val comic: Comic = comicsDAO.createNewComic(series, number, title, availability)
        series.comics.add(comic)
        series.comics.sort()
        updateDisplayedSeries(comic.series)
        return comic
    }

    /**
     * deletes a comic
     */
    fun deleteComic(comic: Comic) {
        comicsDAO.deleteComic(comic)
        comic.series.comics.remove(comic)
        comic.series.comics.sort()
        updateDisplayedSeries(comic.series)
    }

    /**
     * updates a comic
     */
    fun updateComic(comic: Comic) {
        comicsDAO.updateComic(comic)
        updateDisplayedSeries(comic.series)
    }

    /**
     * updates the number of a comic
     */
    fun updateComicNumber(comic: Comic, newNumber: Int) {
        comicsDAO.updateComicNumber(comic, newNumber)
        comic.number = newNumber
        comic.series.comics.sort()
        updateDisplayedSeries(comic.series)
    }

    private fun updateDisplayedSeries(series: Series) {
        val index: Int = displayingList.indexOf(series)
        if (index != SortedList.INVALID_POSITION) {
            displayingList.updateItemAt(index, series)
        }
    }
}