package mcervini.comicslist

import android.content.DialogInterface
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
import mcervini.comicslist.dialogs.EditComicDialogFragment
import mcervini.comicslist.dialogs.EditSeriesDialogFragment
import mcervini.comicslist.dialogs.NewComicDialogFragment
import mcervini.comicslist.dialogs.NewSeriesDialogFragment

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

    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        val info = menuInfo as SeriesRecyclerView.ContextMenuInfo
        menuInflater.inflate(R.menu.modify_delete_context_menu, menu)
        if (info.nestedPosition == -1) {
            menuInflater.inflate(R.menu.series_context_menu, menu)
        }
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
                makeDeleteDialog(comic != null) { _, _ ->
                    if (comic != null) {
                        listUpdater.deleteComic(comic)
                    } else {
                        listUpdater.deleteSeries(series)
                    }
                }.show()
            }

            R.id.menu_edit -> {
                if (comic == null) {
                    EditSeriesDialogFragment(series) { newName: String -> onEditSeriesConfirm(series, newName) }
                            .show(supportFragmentManager, "editDialog")
                } else {
                    EditComicDialogFragment(comic) { title: String, number: Int, availability: Availability, numberChanged: Boolean ->
                        onEditComicConfirm(comic, title, number, availability, numberChanged)
                    }.show(supportFragmentManager, "editDialog")
                }
            }

            R.id.menu_new_comic ->
                NewComicDialogFragment(series) { number: Int, title: String, availability: Availability ->
                    listUpdater.createComic(series, number, title, availability)
                }.show(supportFragmentManager, "newComic")
        }
        return true
    }


    private fun makeDeleteDialog(comic: Boolean, onConfirm: DialogInterface.OnClickListener): AlertDialog {
        return AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete))
                .setMessage(if (comic) {
                    R.string.delete_comic_question
                } else {
                    R.string.delete_series_question
                })
                .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.yes, onConfirm)
                .create()
    }

    private val onNewSeriesEntered: (String, Int, Availability) -> Unit = { name, numberOfComics, availability ->
        listUpdater.createSeries(name, numberOfComics, availability)
    }

    private fun onEditComicConfirm(comic: Comic, title: String, number: Int, availability: Availability, numberChanged: Boolean) {
        comic.availability = availability
        comic.title = title
        listUpdater.updateComic(comic)
        if (numberChanged) {
            listUpdater.updateComicNumber(comic, number)
        }
    }

    private fun onEditSeriesConfirm(series: Series, newName: String) {
        series.name = newName
        listUpdater.updateSeries(series)
    }
}