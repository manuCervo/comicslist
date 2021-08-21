package mcervini.comicslist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.SortedList
import kotlinx.android.synthetic.main.activity_list.*
import mcervini.comicslist.adapters.SeriesListAdapter
import mcervini.comicslist.dialogs.*
import mcervini.comicslist.io.SqliteComicsDAO
import mcervini.comicslist.io.SqliteSeriesDAO
import mcervini.comicslist.io.backup.AsyncExporter
import mcervini.comicslist.io.backup.AsyncImporter
import mcervini.comicslist.io.backup.JsonExporter
import mcervini.comicslist.io.backup.JsonImporter
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ListActivity : AppCompatActivity() {

    private lateinit var list: MutableList<Series>
    private lateinit var displayingList: SortedList<Series>
    private lateinit var seriesDAO: SqliteSeriesDAO
    private lateinit var seriesListAdapter: SeriesListAdapter
    private lateinit var comicsDAO: SqliteComicsDAO
    private lateinit var dataUpdater: DataUpdater

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

        displayingList = SortedList(Series::class.java, sortedListCallback)
        seriesListAdapter = SeriesListAdapter(displayingList)
        comicsRecyclerView.adapter = seriesListAdapter
        registerForContextMenu(comicsRecyclerView)

        dataUpdater = DataUpdater(seriesDAO, comicsDAO, list, displayingList)
        displayingList.addAll(list)

        addSeriesFAB.setOnClickListener {
            NewSeriesDialog(onNewSeriesEntered).show(supportFragmentManager, "NewSeries")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        menu?.let {
            val item = it.findItem(R.id.menu_search)
            val searchView = item.actionView as SearchView
            searchView.setOnQueryTextListener(queryTextListener)
        }
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

        val series: Series = displayingList[info.position]

        val comic: Comic? = if (info.nestedPosition != -1) {
            series.comics[info.nestedPosition]
        } else {
            null
        }

        when (item.itemId) {
            R.id.menu_delete -> {
                makeDeleteDialog(comic != null) {
                    if (comic != null) {
                        dataUpdater.deleteComic(comic)
                    } else {
                        dataUpdater.deleteSeries(series)
                    }
                }.show()
            }

            R.id.menu_edit -> {
                if (comic == null) {
                    EditSeriesDialog(series) { newName: String ->
                        onEditSeriesConfirm(series, newName)
                    }.show(supportFragmentManager, "editDialog")
                } else {
                    EditComicDialog(comic) { title: String, number: Int, availability: Availability, numberChanged: Boolean ->
                        onEditComicConfirm(comic, title, number, availability, numberChanged)
                    }.show(supportFragmentManager, "editDialog")
                }
            }

            R.id.menu_new_comic ->
                NewComicDialog(series) { number: Int, title: String, availability: Availability ->
                    dataUpdater.createComic(series, number, title, availability)
                }.show(supportFragmentManager, "newComic")
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            intent?.data?.let { uri ->
                when (requestCode) {
                    MAKE_BACKUP -> onFragmentResumeAction = { makeBackup(uri) }
                    IMPORT_BACKUP -> onFragmentResumeAction = { importBackup(uri) }
                }
            }
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        onFragmentResumeAction?.let { onResumeAction ->
            onResumeAction()
            onFragmentResumeAction = null
        }
    }

    private val onNewSeriesEntered: (String, Int, Availability) -> Unit =
        { name, numberOfComics, availability ->
            dataUpdater.createSeries(name, numberOfComics, availability)
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
        dataUpdater.updateComic(comic)
        if (numberChanged) {
            dataUpdater.updateComicNumber(comic, number)
        }
    }

    private fun onEditSeriesConfirm(series: Series, newName: String) {
        series.name = newName
        dataUpdater.updateSeries(series)
    }

    private fun importBackup(uri: Uri) {
        val importer = JsonImporter(uri, contentResolver)

        fun startImporting(importMode: AsyncImporter.ImportMode = AsyncImporter.ImportMode.OVERWRITE) {
            AsyncImporter(
                this,
                executor,
                importer,
                seriesDAO,
                comicsDAO,
                list,
                displayingList,
                importMode
            ).run()
        }

        if (list.size == 0) {
            startImporting()
        } else {
            ImportOptionsDialog { importMode ->
                startImporting(importMode)
            }.show(supportFragmentManager, "ImportOptions")
        }
    }

    private fun makeBackup(uri: Uri) {
        AsyncExporter(
            list,
            JsonExporter(uri, contentResolver),
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

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            Log.e("submit", p0 ?: "")
            return false
        }

        override fun onQueryTextChange(query: String?): Boolean {
            Log.e("change", query ?: "")
            return false
        }
    }

    private val sortedListCallback = object : SortedList.Callback<Series>() {
        override fun compare(o1: Series?, o2: Series?): Int {
            if (o1 != null && o2 != null) {
                return o1.compareTo(o2)
            }
            return 0
        }

        override fun onInserted(position: Int, count: Int) {
            seriesListAdapter.notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            seriesListAdapter.notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            seriesListAdapter.notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int) {
            seriesListAdapter.notifyItemRangeChanged(position, count)
        }

        override fun areContentsTheSame(oldItem: Series?, newItem: Series?): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(item1: Series?, item2: Series?): Boolean {
            return item1 === item2
        }
    }
}