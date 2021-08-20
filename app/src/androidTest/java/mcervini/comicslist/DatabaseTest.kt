package mcervini.comicslist

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import mcervini.comicslist.io.Database
import mcervini.comicslist.io.SqliteComicsDAO
import mcervini.comicslist.io.SqliteSeriesDAO
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    @Test
    fun seriesInsertion() {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        clearDatabase(appContext)

        val dao = SqliteSeriesDAO(appContext)

        val series: MutableList<Series> = mutableListOf()
        for (i in 1..10) {
            series.add(dao.createNewSeries("series$i", 5, Availability.BOOKED))
        }
        val result: MutableList<Series> = dao.getAllSeries()

        assertSeriesEqual(series, result)
    }

    @Test
    fun seriesDeletion() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        clearDatabase(appContext)

        val dao = SqliteSeriesDAO(appContext)

        val series: MutableList<Series> = mutableListOf()
        for (i in 1..10) {
            series.add(dao.createNewSeries("series$i", 5, Availability.BOOKED))
        }


        val toDelete = listOf<Series>(series[9], series[5], series[1], series[4], series[7])

        for (s in toDelete) {
            dao.deleteSeries(s)
            series.remove(s)
        }

        val result = dao.getAllSeries()

        assertSeriesEqual(series, result)
    }

    @Test
    fun seriesUpdating() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        clearDatabase(appContext)

        val dao = SqliteSeriesDAO(appContext)

        val series: MutableList<Series> = mutableListOf()
        for (i in 1..10) {
            series.add(dao.createNewSeries("series$i", 5, Availability.BOOKED))
        }

        series[3].name = "aaaa"
        series[7].name = "bbbb"
        series[1].name = "cccc"
        series[5].name = "dddd"
        series[9].name = "eeee"

        for (s in series) {
            dao.updateSeries(s)
        }

        val result = dao.getAllSeries()

        assertSeriesEqual(series, result)

    }

    @Test
    fun comicsInsertion() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        clearDatabase(appContext)


        val dao = SqliteComicsDAO(appContext)
        val seriesDAO = SqliteSeriesDAO(appContext)
        val comics: MutableList<Comic> = mutableListOf()

        val series: MutableList<Series> = mutableListOf()
        for (i in 1..5) {
            series.add(seriesDAO.createNewSeries("series$i", 0, Availability.BOOKED))
        }




        for (s in series) {
            for (i in 1..10) {
                comics.add(dao.createNewComic(s, i, "comic$i", Availability.BOOKED))
            }
        }

        val result = dao.getAllComics()

        assertComicsEquals(comics, result)
    }

    @Test
    fun comicUpdating() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        clearDatabase(appContext)

        val dao = SqliteComicsDAO(appContext)
        val seriesDAO = SqliteSeriesDAO(appContext)
        val comics: MutableList<Comic> = mutableListOf()

        val series: MutableList<Series> = mutableListOf()
        for (i in 1..5) {
            series.add(seriesDAO.createNewSeries("series$i", 0, Availability.BOOKED))
        }

        for (s in series) {
            for (i in 1..2) {
                comics.add(dao.createNewComic(s, i, "comic$i", Availability.BOOKED))
            }
        }

        for (i in 0 until series.size step 2) {
            comics[i].title = "aaaaaaaaa$i"
            comics[i].availability = Availability.NOT_AVAILABLE
        }

        for (c in comics) {
            dao.updateComic(c)
        }

        var result = dao.getAllComics()

        assertComicsEquals(result, comics)


        for (i in 0 until series.size step 2) {
            dao.updateComicNumber(comics[i], 1000 + i)
            comics[i].number = 1000 + i
        }


        result = dao.getAllComics()

        assertComicsEquals(result, comics)

    }

    @Test
    fun comicDeletion() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        clearDatabase(appContext)

        val dao = SqliteComicsDAO(appContext)
        val seriesDAO = SqliteSeriesDAO(appContext)
        val comics: MutableList<Comic> = mutableListOf()

        val series: MutableList<Series> = mutableListOf()
        for (i in 1..5) {
            series.add(seriesDAO.createNewSeries("series$i", 0, Availability.BOOKED))
        }

        for (s in series) {
            for (i in 1..10) {
                comics.add(dao.createNewComic(s, i, "comic$i", Availability.BOOKED))
            }
        }

        val toDelete = comics.filterIndexed { index, comic -> index % 2 == 0 }

        for (c in toDelete) {
            dao.deleteComic(c)
            comics.remove(c)
        }

        val result = dao.getAllComics()

        assertComicsEquals(result, comics)

    }

    private fun assertComicsEquals(l1: List<Comic>, l2: List<Comic>) {
        assert(l1.size == l2.size)
        val s1 = l1.sortedWith { c1, c2 -> c1.number.compareTo(c2.number) }
        val s2 = l2.sortedWith { c1, c2 -> c1.number.compareTo(c2.number) }

        for (i in s1.indices) {
            val c1: Comic = s1[i]
            val c2: Comic = s2[i]

            assert(c1.number == c2.number)
            assert(c1.title.equals(c2.title))
            assert(c1.availability == c2.availability)
            assert(c1.series.id.equals(c2.series.id))
            assert(c1.series.name.equals(c2.series.name))
        }
    }

    private fun clearDatabase(context: Context) {
        val database: Database = Database(context)
        database.delete("comic", null)
        database.delete("series", null)
    }


    private fun assertSeriesEqual(list1: List<Series>, list2: List<Series>) {
        assert(list1.size == list2.size)

        val s1 = list1.sortedWith { o1, o2 -> o1.name.compareTo(o2.name) }
        val s2 = list2.sortedWith { o1, o2 -> o1.name.compareTo(o2.name) }

        for (i in s1.indices) {
            val series1 = s1[i]
            val series2 = s2[i]

            assert(series1.id.equals(series2.id))
            assert(series1.name.equals(series2.name))
            assertComicsEquals(series1.comics, series2.comics)
        }
    }
}