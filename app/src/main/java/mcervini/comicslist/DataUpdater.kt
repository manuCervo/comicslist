package mcervini.comicslist

import androidx.recyclerview.widget.SortedList
import mcervini.comicslist.io.ComicsDAO
import mcervini.comicslist.io.SeriesDAO

class DataUpdater(
    private val seriesDAO: SeriesDAO,
    private val comicsDAO: ComicsDAO,
    private val list: MutableList<Series>,
    private val displayingList: SortedList<Series>
) {
    fun createSeries(name: String, numberOfComics: Int, availability: Availability): Series {
        val series: Series = seriesDAO.createNewSeries(name, numberOfComics, availability)
        list.add(series)
        displayingList.add(series)
        return series
    }

    fun deleteSeries(series: Series) {
        seriesDAO.deleteSeries(series)
        list.remove(series)
        displayingList.remove(series)
    }

    fun updateSeries(series: Series) {
        seriesDAO.updateSeries(series)
        updateDisplayedSeries(series)
    }

    fun createComic(series: Series, number: Int, title: String, availability: Availability): Comic {
        val comic: Comic = comicsDAO.createNewComic(series, number, title, availability)
        series.comics.add(comic)
        series.comics.sort()
        updateDisplayedSeries(comic.series)
        return comic
    }

    fun deleteComic(comic: Comic) {
        comicsDAO.deleteComic(comic)
        comic.series.comics.remove(comic)
        comic.series.comics.sort()
        updateDisplayedSeries(comic.series)
    }

    fun updateComic(comic: Comic) {
        comicsDAO.updateComic(comic)
        updateDisplayedSeries(comic.series)
    }

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