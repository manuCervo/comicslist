package mcervini.comicslist.io

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.core.database.getIntOrNull
import mcervini.comicslist.Availability
import mcervini.comicslist.Comic
import mcervini.comicslist.Series
import java.util.*

class SqliteSeriesDAO(val context: Context) : SeriesDAO {


    private val database: Database = Database(context)


    override fun getAllSeries(): MutableList<Series> {
        val result: Cursor =
            database.query("series LEFT JOIN comic ON comic.series_id = series.id", null, null)
        val allSeries: MutableMap<UUID, Series> = mutableMapOf()

        while (result.moveToNext()) {
            val seriesId: UUID =
                UUID.fromString(result.getString(result.getColumnIndex("id")))

            val series: Series = allSeries[seriesId] ?: run {
                Series(
                    seriesId,
                    result.getString(result.getColumnIndex("name"))
                ).also { allSeries[seriesId] = it }
            }

            result.getIntOrNull(result.getColumnIndex("number"))?.let { number ->
                val title: String = result.getString(result.getColumnIndex("title"))
                val availability: Availability =
                    Availability.fromValue(result.getInt(result.getColumnIndex("availability")))

                series.comics.add(Comic(series, number, title, availability))
            }

        }
        result.close()
        return allSeries.values.toMutableList()
    }

    override fun createNewSeries(
        name: String,
        numberOfComics: Int,
        availability: Availability
    ): Series {

        val uuid: UUID = UUID.randomUUID()
        val series: Series = Series(uuid, name)

        val seriesValues: ContentValues = seriesToContentValues(series)
        database.insert("series", seriesValues)

        val comicsDAO: ComicsDAO = SqliteComicsDAO(context)
        for (i in 1..numberOfComics) {
            val comic: Comic = comicsDAO.createNewComic(series, i, "", availability)
            series.comics.add(comic)
        }

        return series
    }

    override fun updateSeries(series: Series) {
        val values: ContentValues = seriesToContentValues(series)
        database.update("series", values, "id = '${series.id}'")
    }

    override fun deleteSeries(series: Series) {
        database.delete("series", "id = '${series.id}'")
    }

    override fun addExistingSeries(series: Series) {
        val values: ContentValues = seriesToContentValues(series)
        database.insert("series", values)
    }

    private fun seriesToContentValues(series: Series): ContentValues {
        return ContentValues().apply {
            put("name", series.name)
            put("id", series.id.toString())
        }
    }
}