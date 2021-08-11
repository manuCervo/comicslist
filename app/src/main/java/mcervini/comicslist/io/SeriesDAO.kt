package mcervini.comicslist.io

import mcervini.comicslist.Availability
import mcervini.comicslist.Series

interface SeriesDAO {
    fun getAllSeries(): MutableList<Series>
    fun createNewSeries(name: String, numberOfComics: Int, availability: Availability): Series
    fun updateSeries(series: Series)
    fun deleteSeries(series: Series)
}