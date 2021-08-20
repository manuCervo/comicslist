package mcervini.comicslist.io

import android.content.ContentValues
import android.content.Context
import mcervini.comicslist.Availability
import mcervini.comicslist.Comic
import mcervini.comicslist.Series

class SqliteComicsDAO(private val context: Context) : ComicsDAO {
    private val database: Database = Database(context)

    override fun getAllComics(): MutableList<Comic> {
        val seriesDao: SeriesDAO = SqliteSeriesDAO(context)
        val series: MutableList<Series> = seriesDao.getAllSeries()
        val comics: MutableList<Comic> = mutableListOf()
        for (s in series) {
            comics.addAll(s.comics)
        }
        return comics
    }

    override fun createNewComic(
        series: Series,
        number: Int,
        title: String,
        availability: Availability
    ): Comic {
        val comic: Comic = Comic(series, number, title, availability)
        val values: ContentValues = comicToContentValues(comic)
        database.insert("comic", values)
        return comic
    }

    override fun updateComic(comic: Comic) {
        database.update(
            "comic",
            comicToContentValues(comic),
            "number = ${comic.number} AND series_id = '${comic.series.id}'"
        )
    }

    override fun deleteComic(comic: Comic) {
        database.delete("comic", "number = ${comic.number} AND series_id = '${comic.series.id}'")
    }

    override fun updateComicNumber(comic: Comic, newNumber: Int) {
        val values: ContentValues = comicToContentValues(comic.copy(number = newNumber))
        database.update(
            "comic",
            values,
            "number = ${comic.number} AND series_id = '${comic.series.id}'"
        )
    }

    override fun addExistingComic(comic: Comic) {
        val values = comicToContentValues(comic)
        database.insert("comic", values)
    }

    private fun comicToContentValues(comic: Comic): ContentValues {

        return ContentValues().apply {
            put("series_id", comic.series.id.toString())
            put("number", comic.number)
            put("title", comic.title)
            put("availability", comic.availability.value)
        }

    }
}