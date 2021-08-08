package mcervini.comicslist

import mcervini.comicslist.adapters.SeriesListAdapter
import mcervini.comicslist.data.ComicsDAO
import mcervini.comicslist.data.SeriesDAO

class ListUpdater(private val seriesDAO: SeriesDAO, private val comicsDAO: ComicsDAO, private val adapter: SeriesListAdapter, private val list: MutableList<Series>) {
    fun createSeries(name: String, numberOfComics: Int, availability: Availability): Series {
        val series: Series = seriesDAO.createNewSeries(name, numberOfComics, availability)
        list.add(series)
        adapter.notifyDataSetChanged()
        return series
    }

    fun deleteSeries(series: Series) {
        seriesDAO.deleteSeries(series)
        list.remove(series)
        adapter.notifyDataSetChanged()
    }

    fun updateSeries(series: Series) {
        seriesDAO.updateSeries(series)
        adapter.notifyDataSetChanged()
    }

    fun createComic(series: Series, number: Int, title: String, availability: Availability): Comic {
        val comic: Comic = comicsDAO.createNewComic(series, number, title, availability)
        series.comics.add(comic)
        adapter.notifyDataSetChanged()
        return comic
    }

    fun deleteComic(comic: Comic) {
        comicsDAO.deleteComic(comic)
        comic.series.comics.remove(comic)
        adapter.notifyDataSetChanged()
    }

    fun updateComic(comic: Comic) {
        comicsDAO.updateComic(comic)
        adapter.notifyDataSetChanged()
    }

    fun updateComicNumber(comic: Comic, newNumber: Int) {
        comicsDAO.updateComicNumber(comic, newNumber)
        adapter.notifyDataSetChanged()
    }
}