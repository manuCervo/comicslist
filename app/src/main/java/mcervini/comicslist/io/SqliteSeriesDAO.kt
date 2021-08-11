package mcervini.comicslist.io

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import mcervini.comicslist.Availability
import mcervini.comicslist.Comic
import mcervini.comicslist.Series
import java.util.*

class SqliteSeriesDAO(val context: Context) : SeriesDAO {


    private val database: Database = Database(context)


    override fun getAllSeries(): MutableList<Series> {
        val result: Cursor = database.query("series,comic", null, "comic.series_id = series.id")
        val allSeries: MutableMap<UUID, Series> = mutableMapOf()

        while (result.moveToNext()) {
            val seriesId: UUID = UUID.fromString(result.getString(result.getColumnIndex("series_id")))

            if (!allSeries.containsKey(seriesId)) {
                val name: String = result.getString(result.getColumnIndex("name"))
                allSeries[seriesId] = Series(seriesId, name)
            }

            val series: Series = allSeries[seriesId] ?: throw IllegalStateException()

            val number: Int = result.getInt(result.getColumnIndex("number"))
            val title: String = result.getString(result.getColumnIndex("title"))
//            val availability: Availability = when (result.getInt(result.getColumnIndex("availability"))) {
//                0 -> Availability.AVAILABLE
//                1 -> Availability.NOT_AVAILABLE
//                2 -> Availability.BOOKED
//                else -> throw IllegalArgumentException()
//        }
            val availability: Availability = Availability.fromValue(result.getInt(result.getColumnIndex("availability")))

            series.comics.add(Comic(series, number, title, availability))
        }
        result.close()
        return allSeries.values.toMutableList()
    }

    override fun createNewSeries(name: String, numberOfComics: Int, availability: Availability): Series {

        val uuid: UUID = UUID.randomUUID()


        val series: Series = Series(uuid, name)

        val comicsDAO: ComicsDAO = SqliteComicsDAO(context)
        for (i in 1..numberOfComics) {
            val comic: Comic = comicsDAO.createNewComic(series, i, "", availability)
            series.comics.add(comic)
        }

        val seriesValues: ContentValues = seriesToContentValues(series)

        database.insert("series", seriesValues)
        return series
    }

    override fun updateSeries(series: Series) {
        val values: ContentValues = seriesToContentValues(series)
        database.update("series", values, "id = '${series.id}'")
    }

    override fun deleteSeries(series: Series) {
        database.delete("series", "id = '${series.id}'")
    }

    private fun seriesToContentValues(series: Series): ContentValues {
        val contentValues: ContentValues = ContentValues()
        contentValues.put("name", series.name)
        contentValues.put("id", series.id.toString())
        return contentValues
    }
}