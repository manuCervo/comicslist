package mcervini.comicslist

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import mcervini.comicslist.adapters.SeriesListAdapter
import mcervini.comicslist.data.SeriesDAO
import mcervini.comicslist.data.SqliteSeriesDAO

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        val seriesDao: SeriesDAO = SqliteSeriesDAO(applicationContext)

        val adapter: SeriesListAdapter = SeriesListAdapter(seriesDao.getAllSeries())

        comicsRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }
}