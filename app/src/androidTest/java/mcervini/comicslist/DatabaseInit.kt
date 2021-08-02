package mcervini.comicslist

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import mcervini.comicslist.data.Database
import mcervini.comicslist.data.SeriesDAO
import mcervini.comicslist.data.SqliteComicsDAO
import mcervini.comicslist.data.SqliteSeriesDAO
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DatabaseInit {
    private val context:Context = InstrumentationRegistry.getInstrumentation().targetContext
    @Test
    fun clear() {
        val database: Database = Database(context)
        database.delete("comic", null)
        database.delete("series", null)
    }

    @Test
    fun populate()
    {
        val seriesDAO:SqliteSeriesDAO = SqliteSeriesDAO(context)
        val comicsDAO:SqliteComicsDAO = SqliteComicsDAO(context)

        seriesDAO.createNewSeries("series1",5,Availability.AVAILABLE)
        seriesDAO.createNewSeries("series2",5,Availability.NOT_AVAILABLE)
        seriesDAO.createNewSeries("series3",5,Availability.BOOKED)

        val s1 = seriesDAO.createNewSeries("series4",5,Availability.AVAILABLE)
        for (c in s1.comics)
        {
            c.title = "comic${s1.comics.indexOf(c)}"
            comicsDAO.updateComic(c)
        }

        val s2 = seriesDAO.createNewSeries("series5",5,Availability.AVAILABLE)

        for(c in s2.comics)
        {
            val i = s2.comics.indexOf(c)

            if(i%2 == 0)
            {
                c.title = "comic$i"
                c.availability = Availability.BOOKED
                comicsDAO.updateComic(c)
            }
        }

    }
}