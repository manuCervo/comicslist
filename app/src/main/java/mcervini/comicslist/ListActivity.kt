package mcervini.comicslist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_list.*
import mcervini.comicslist.adapters.SeriesListAdapter
import mcervini.comicslist.dialogs.EditComicDialogFragment
import mcervini.comicslist.dialogs.EditSeriesDialogFragment
import mcervini.comicslist.dialogs.NewComicDialogFragment
import mcervini.comicslist.dialogs.NewSeriesDialogFragment
import mcervini.comicslist.io.AsyncExporter
import mcervini.comicslist.io.AsyncImporter
import mcervini.comicslist.io.SqliteComicsDAO
import mcervini.comicslist.io.SqliteSeriesDAO
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ListActivity : AppCompatActivity() {

    lateinit var list: MutableList<Series>
    lateinit var seriesDAO: SqliteSeriesDAO
    lateinit var seriesListAdapter: SeriesListAdapter
    lateinit var comicsDAO: SqliteComicsDAO
    lateinit var listUpdater: ListUpdater

    private val executor: Executor = Executors.newSingleThreadExecutor()
    private var onFragmentResumeAction: (() -> Unit)? = null

    companion object {
        private const val MAKE_BACKUP = 0
        private const val IMPORT_BACKUP = 1
    }

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

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        val info = menuInfo as SeriesRecyclerView.ContextMenuInfo
        menuInflater.inflate(R.menu.modify_delete_context_menu, menu)
        if (info.nestedPosition == -1) {
            menuInflater.inflate(R.menu.series_context_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add -> NewSeriesDialogFragment(onNewSeriesEntered).show(
                supportFragmentManager,
                "dialog"
            )
            R.id.menu_make_backup -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = ("application/json")
                    putExtra(Intent.EXTRA_TITLE, "backup.json")
                }
                startActivityForResult(intent, MAKE_BACKUP)
            }
            R.id.menu_import_backup -> {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = ("application/json")
                }
                startActivityForResult(intent, IMPORT_BACKUP)
            }
        }
        return true
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
                makeDeleteDialog(comic != null) {
                    if (comic != null) {
                        listUpdater.deleteComic(comic)
                    } else {
                        listUpdater.deleteSeries(series)
                    }
                }.show()
            }

            R.id.menu_edit -> {
                if (comic == null) {
                    EditSeriesDialogFragment(series) { newName: String ->
                        onEditSeriesConfirm(
                            series,
                            newName
                        )
                    }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MAKE_BACKUP -> {
                if (resultCode == Activity.RESULT_OK) {
                    onFragmentResumeAction = { makeBackup(data?.data!!) }
                }
            }
            IMPORT_BACKUP -> {
                if (resultCode == Activity.RESULT_OK) {
                    onFragmentResumeAction = { importBackup(data?.data!!) }
                }
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (onFragmentResumeAction != null) {
            onFragmentResumeAction?.invoke()
            onFragmentResumeAction = null
        }
    }

    private val onNewSeriesEntered: (String, Int, Availability) -> Unit =
        { name, numberOfComics, availability ->
            listUpdater.createSeries(name, numberOfComics, availability)
        }

    private fun onEditComicConfirm(
        comic: Comic,
        title: String,
        number: Int,
        availability: Availability,
        numberChanged: Boolean
    ) {
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

    private fun importBackup(uri: Uri) {
        AsyncImporter(
            this,
            applicationContext.contentResolver.openInputStream(uri)!!,
            list,
            seriesDAO,
            comicsDAO,
            seriesListAdapter,
            executor
        ).run()
    }

    private fun makeBackup(uri: Uri) {
        AsyncExporter(
            list,
            applicationContext.contentResolver.openOutputStream(uri)!!,
            this,
            executor
        ).run()
    }

    private fun makeDeleteDialog(comic: Boolean, onConfirm: () -> Unit): AlertDialog {
        return AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete))
            .setMessage(
                if (comic) {
                    R.string.delete_comic_question
                } else {
                    R.string.delete_series_question
                }
            )
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.yes) { _, _ -> onConfirm() }
            .create()
    }
}