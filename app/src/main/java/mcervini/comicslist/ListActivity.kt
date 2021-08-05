package mcervini.comicslist

import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import mcervini.comicslist.adapters.SeriesListAdapter
import mcervini.comicslist.data.SqliteSeriesDAO

class ListActivity : AppCompatActivity() {
    lateinit var list: MutableList<Series>
    lateinit var seriesDAO: SqliteSeriesDAO
    lateinit var seriesListAdapter: SeriesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        seriesDAO = SqliteSeriesDAO(applicationContext)
        list = seriesDAO.getAllSeries()
        seriesListAdapter = SeriesListAdapter(list)

        comicsRecyclerView.adapter = seriesListAdapter
        registerForContextMenu(comicsRecyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> NewSeriesDialogFragment(onNewSeriesEntered).show(supportFragmentManager, "dialog")
        }
        return true
    }

    private val onNewSeriesEntered: (String, Int, Availability) -> Unit = { name, numberOfComics, availability ->
        val newSeries: Series = seriesDAO.createNewSeries(name, numberOfComics, availability)
        list.add(newSeries)
        seriesListAdapter.notifyDataSetChanged()
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        menuInflater.inflate(R.menu.series_context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as SeriesRecyclerView.ContextMenuInfo

        val series: Series = list[info.position]
        val comic: Comic? = if (info.nestedPosition != -1) {
            series.comics[info.nestedPosition]
        } else {
            null
        }

        Log.e("series", series.toString())
        Log.e("comic", comic.toString())

        return true
    }
}