package mcervini.comicslist

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
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

/**
 * main activity of the application
 */
class ListActivity : AppCompatActivity() {

    //list of series that are currently stored in the database
    private lateinit var databaseList: MutableList<Series>

    //list of series that are currently shown
    private lateinit var displayingList: SortedList<Series>

    private lateinit var seriesDAO: SqliteSeriesDAO
    private lateinit var seriesListAdapter: SeriesListAdapter
    private lateinit var comicsDAO: SqliteComicsDAO
    private lateinit var dataUpdater: DataUpdater
    private lateinit var filter: SeriesListFilter


    private var openSearchView: Boolean = false
    private var missingOnly: Boolean = false

    //executor for running background threads
    private val executor: Executor = Executors.newSingleThreadExecutor()

    //used to show dialogs after the onActivityResult event
    private var onFragmentResumeAction: (() -> Unit)? = null

    companion object {
        //request codes for intents
        private const val MAKE_BACKUP = 0
        private const val IMPORT_BACKUP = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        intent.extras?.let {
            openSearchView = it.getBoolean("search", false)
            missingOnly = it.getBoolean("missingOnly", false)
        }

        seriesDAO = SqliteSeriesDAO(applicationContext)
        comicsDAO = SqliteComicsDAO(applicationContext)
        databaseList = seriesDAO.getAllSeries()
        println(databaseList.size)

        displayingList = SortedList(Series::class.java, sortedListCallback)
        seriesListAdapter = SeriesListAdapter(displayingList)
        seriesRecyclerView.adapter = seriesListAdapter
        seriesRecyclerView.addItemDecoration(
            DividerItemDecoration(
                applicationContext,
                LinearLayout.VERTICAL
            )
        )
        registerForContextMenu(seriesRecyclerView)

        dataUpdater = DataUpdater(seriesDAO, comicsDAO, databaseList, displayingList)
        displayingList.addAll(databaseList)

        filter = SeriesListFilter(databaseList, displayingList)

        addSeriesFAB.setOnClickListener {
            NewSeriesDialog(onNewSeriesEntered).show(supportFragmentManager, "NewSeries")
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)

        val searchView = (menu!!.findItem(R.id.menu_search)!!.actionView as SearchView)

        searchView.setOnQueryTextListener(queryTextListener)

        searchView.setOnSearchClickListener {
            addSeriesFAB.visibility = GONE
        }

        searchView.setOnCloseListener {
            filter.clearNameFilter()
            addSeriesFAB.visibility = VISIBLE
            false
        }

        if (openSearchView) {
            searchView.isIconified = false
        }


        if (missingOnly) {
            menu.findItem(R.id.menu_show_available).isChecked = false
            menu.findItem(R.id.menu_show_booked).isChecked = false
            filter.excludeAvailable(true)
            filter.excludeBooked(true)
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

        /**
         *forces a menuItem to not close after being touched by adding a fake action view to it
         */
        fun keepMenuOpen(item: MenuItem) {
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
            item.actionView = View(this)
            item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    return false
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    return false
                }
            })
        }

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
            R.id.menu_show_available -> {
                item.isChecked = !item.isChecked
                filter.excludeAvailable(!item.isChecked)
                keepMenuOpen(item)
                return false
            }
            R.id.menu_show_booked -> {
                item.isChecked = !item.isChecked
                filter.excludeBooked(!item.isChecked)
                keepMenuOpen(item)
                return false
            }
            R.id.menu_show_not_available -> {
                item.isChecked = !item.isChecked
                filter.excludeNotAvailable(!item.isChecked)
                keepMenuOpen(item)
                return false
            }
        }
        return true
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as SeriesRecyclerView.ContextMenuInfo

        val displayingSeries: Series = displayingList[info.position]

        val series: Series = if (filter.isFiltering) {
            filter.getSeries(displayingSeries.id)
        } else {
            displayingSeries
        }

        val comic: Comic? = displayingSeries.comics.getOrNull(info.nestedPosition)

        fun deleteComic() {
            dataUpdater.deleteComic(comic!!)

            if (displayingSeries !== series) {
                displayingSeries.comics.remove(comic)
                displayingList.updateItemAt(
                    displayingList.indexOf(displayingSeries),
                    displayingSeries
                )
            }
        }

        fun deleteSeries() {
            dataUpdater.deleteSeries(series)
            if (displayingSeries !== series) {
                displayingList.remove(displayingSeries)
            }
        }

        fun updateSeries(newName: String) {
            onEditSeriesConfirm(series, newName)
            if (displayingSeries !== series) {
                displayingSeries.name = newName
                displayingList.updateItemAt(
                    displayingList.indexOf(displayingSeries),
                    displayingSeries
                )
            }
        }

        fun updateComic(
            title: String,
            number: Int,
            availability: Availability,
            numberChanged: Boolean
        ) {
            onEditComicConfirm(comic!!, title, number, availability, numberChanged)
            if (displayingSeries !== series) {
                displayingList.updateItemAt(
                    displayingList.indexOf(displayingSeries),
                    displayingSeries
                )
            }

        }

        fun insertComic(number: Int, title: String, availability: Availability) {
            val c = dataUpdater.createComic(series, number, title, availability)
            if (displayingSeries !== series) {
                displayingSeries.comics.add(c)
                displayingSeries.comics.sort()
                displayingList.updateItemAt(
                    displayingList.indexOf(displayingSeries),
                    displayingSeries
                )
            }
        }

        when (item.itemId) {
            R.id.menu_delete -> {
                comic?.run { DeleteDialog(R.string.delete_comic_question) { deleteComic() } }
                    ?: run { DeleteDialog(R.string.delete_series_question) { deleteSeries() } }
            }
            R.id.menu_edit -> {
                comic?.run {
                    EditComicDialog(comic) { title: String, number: Int, availability: Availability, numberChanged: Boolean ->
                        updateComic(title, number, availability, numberChanged)
                    }
                } ?: run { EditSeriesDialog(series) { newName: String -> updateSeries(newName) } }
            }
            R.id.menu_new_comic ->
                NewComicDialog(series) { number: Int, title: String, availability: Availability ->
                    insertComic(number, title, availability)
                }
            else -> null
        }?.show(supportFragmentManager, "contextOptionDialog")
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK) {
            intent?.data?.let { uri ->
                //dialogFragments cannot be shown from here, so onFragmentResume action must be set to run in the onResumeFragment event
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

    /**
     *called when the user confirmed the insertion of a new series
     */

    private val onNewSeriesEntered: (String, Int, Availability) -> Unit =
        { name, numberOfComics, availability ->
            dataUpdater.createSeries(name, numberOfComics, availability)
        }

    /**
     *called when the user confirmed the editing of a comic
     */

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

    /**
     * called when the user confirmed the editing of a comic
     */
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
                databaseList,
                displayingList,
                importMode
            ).run()
        }

        if (databaseList.size == 0) {
            startImporting()
        } else {
            ImportOptionsDialog { importMode ->
                startImporting(importMode)
            }.show(supportFragmentManager, "ImportOptions")
        }
    }

    private fun makeBackup(uri: Uri) {
        AsyncExporter(
            databaseList,
            JsonExporter(uri, contentResolver),
            this,
            executor
        ).run()
    }

    /**
     *listener for when the user uses the search bar
     */

    private val queryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            filter.filterByName(query ?: "")
            return false
        }

        override fun onQueryTextChange(query: String?): Boolean {
            filter.filterByName(query ?: "")
            return false
        }
    }

    //callbacks for automatically update the items in the recyclcerView when the list of the displayed series is updated
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