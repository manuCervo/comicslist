package mcervini.comicslist

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import mcervini.comicslist.adapters.SeriesListAdapter
import mcervini.comicslist.data.SqliteComicsDAO
import mcervini.comicslist.data.SqliteSeriesDAO

class ListActivity : AppCompatActivity() {
    lateinit var list: MutableList<Series>
    lateinit var seriesDAO: SqliteSeriesDAO
    lateinit var seriesListAdapter: SeriesListAdapter
    lateinit var comicsDAO: SqliteComicsDAO
    lateinit var listUpdater: ListUpdater

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        seriesDAO = SqliteSeriesDAO(applicationContext)
        comicsDAO = SqliteComicsDAO(applicationContext)

        list = seriesDAO.getAllSeries()
        seriesListAdapter = SeriesListAdapter(list)

        comicsRecyclerView.adapter = seriesListAdapter


        listUpdater = ListUpdater(seriesDAO, comicsDAO, seriesListAdapter, list)

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
        listUpdater.createSeries(name, numberOfComics, availability)
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

        when (item.itemId) {
            R.id.menu_delete -> {
                val dialog: AlertDialog = AlertDialog.Builder(this)
                        .setTitle("elimina")
                        .setMessage("eliminare ${
                            if (comic != null) {
                                "fumetto"
                            } else {
                                "serie"
                            }
                        }?")
                        .setNegativeButton("no") { dialog, _ -> dialog.dismiss() }
                        .setPositiveButton("sì") { dialog, _ ->
                            if (comic != null) {
                                listUpdater.deleteComic(comic)
                            } else {
                                listUpdater.deleteSeries(series)
                            }
                        }
                        .create()
                dialog.show()
            }
            R.id.menu_edit -> {
                if (comic == null) {
                    EditSeriesDialogFragment(series) { newName: String ->
                        series.name = newName
                        listUpdater.updateSeries(series)
                    }.show(supportFragmentManager, "editDialog")
                }
            }
        }
        return true
    }
}