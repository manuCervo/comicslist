package mcervini.comicslist.io.backup

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.SortedList
import mcervini.comicslist.BackgroundTask
import mcervini.comicslist.Comic
import mcervini.comicslist.R
import mcervini.comicslist.Series
import mcervini.comicslist.io.ComicsDAO
import mcervini.comicslist.io.SeriesDAO
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor


/**
 * asynchronously imports a list of series, while showing a ProgressDialog
 *
 * @param importer the importer to use for importing the series
 * @param executor an Executor used for running the importing process in a background thread
 * @param seriesDAO the DAO for adding the imported series
 * @param comicsDAO the DAO for adding the imported comics
 * @param list the list containing the current series
 * @param displayingList the list containing the series that are currently shown
 * @param mode the import mode
 */
class AsyncImporter(
    activity: AppCompatActivity,
    executor: Executor,
    private val importer: Importer,
    private val seriesDAO: SeriesDAO,
    private val comicsDAO: ComicsDAO,
    private val list: MutableList<Series>,
    private val displayingList: SortedList<Series>,
    private val mode: ImportMode = ImportMode.OVERWRITE
) : BackgroundTask(
    activity, executor,
    R.string.importing_backup
) {
    override fun doTask() {
        val result: Result<List<Series>> = importer.runCatching {
            import()
        }

        if (result.isFailure) {
            @StringRes val error: Int = when (result.exceptionOrNull() as Exception) {
                is IOException -> {
                    R.string.cant_read_file
                }
                is Importer.MissingDataException -> {
                    R.string.import_missing_data
                }
                else -> {
                    R.string.import_error_generic
                }
            }
            activity.runOnUiThread {
                Toast.makeText(
                    activity.applicationContext,
                    error,
                    Toast.LENGTH_LONG
                ).show()
            }
            return
        }

        val imported: List<Series> = result.getOrThrow()

        activity.runOnUiThread {
            progressDialog.setMax(imported.size)
            progressDialog.setIndeterminate(false)
        }

        when (mode) {
            ImportMode.OVERWRITE -> overwriteExisting(imported)
            ImportMode.KEEP -> keepExisting(imported)
            ImportMode.REPLACE -> replace(imported)
            ImportMode.KEEP_BOTH -> keepBoth(imported)
        }
        activity.runOnUiThread {
            displayingList.beginBatchedUpdates()
            displayingList.clear()
            displayingList.addAll(list)
            displayingList.endBatchedUpdates()
        }
    }

    private fun updateProgress(progress: Int) {
        activity.runOnUiThread {
            progressDialog.setProgress(progress)
        }
    }

    private fun listToMaps(): Pair<Map<UUID, Series>, Map<Pair<UUID, Int>, Comic>> {
        val currentSeries = mutableMapOf<UUID, Series>()
        val currentComics = mutableMapOf<Pair<UUID, Int>, Comic>()


        for (s in list) {
            currentSeries[s.id] = s
            for (c in s.comics) {
                currentComics[Pair(s.id, c.number)] = c
            }
        }
        return Pair(currentSeries, currentComics)
    }

    private fun keepBoth(imported: List<Series>) {
        val (currentSeries, _) = listToMaps()

        for ((progress, s) in imported.withIndex()) {

            val seriesId: UUID = s.id
            val series: Series? = currentSeries[seriesId]

            if (series != null) {
                val newSeries = s.copy(id = UUID.randomUUID(), comics = mutableListOf())
                seriesDAO.addExistingSeries(newSeries)
                for (c in s.comics) {
                    val newComic = c.copy(series = newSeries)
                    comicsDAO.addExistingComic(newComic)
                    newSeries.comics.add(newComic)
                }
                list.add(newSeries)
            } else {
                seriesDAO.addExistingSeries(s)
                list.add(s)
                for (c in s.comics) {
                    comicsDAO.addExistingComic(c)
                }
            }
            updateProgress(progress)
        }
    }

    private fun keepExisting(imported: List<Series>) {
        val (currentSeries, currentComics) = listToMaps()

        for ((progress, s) in imported.withIndex()) {

            val seriesId: UUID = s.id
            val series: Series? = currentSeries[seriesId]

            if (series != null) {
                for (c in s.comics) {
                    val key = Pair(seriesId, c.number)
                    val comic: Comic? = currentComics[key]
                    if (comic == null) {
                        series.comics.add(c)
                        comicsDAO.addExistingComic(c)
                    }
                }
            } else {
                seriesDAO.addExistingSeries(s)
                list.add(s)
                for (c in s.comics) {
                    comicsDAO.addExistingComic(c)
                }
            }

            updateProgress(progress)
        }
    }

    private fun overwriteExisting(imported: List<Series>) {
        val (currentSeries, currentComics) = listToMaps()
        for ((progress, s) in imported.withIndex()) {
            val seriesId: UUID = s.id
            val series: Series? = currentSeries[seriesId]

            if (series != null) {
                if (series != s) {
                    series.name = s.name
                    seriesDAO.updateSeries(series)
                }

                for (c in s.comics) {
                    val key = Pair(seriesId, c.number)
                    val comic: Comic? = currentComics[key]

                    if (comic != null) {
                        comic.title = c.title
                        comic.availability = c.availability
                        comicsDAO.updateComic(comic)
                    } else {
                        series.comics.add(c)
                        comicsDAO.addExistingComic(c)
                    }
                }

            } else {
                seriesDAO.addExistingSeries(s)
                list.add(s)
                for (c in s.comics) {
                    comicsDAO.addExistingComic(c)
                }
            }
            updateProgress(progress)
        }
    }

    private fun replace(imported: List<Series>) {
        for (s in list) {
            seriesDAO.deleteSeries(s)
        }

        list.clear()
        list.addAll(imported)

        for ((progress, s) in list.withIndex()) {
            seriesDAO.addExistingSeries(s)
            for (c in s.comics) {
                comicsDAO.addExistingComic(c)
            }
            updateProgress(progress)
        }
    }

    /**
     * specifies how to merge the imported series with the existing ones
     */
    enum class ImportMode {

        /**
         * if there is a series or a comic that is both in the backup and in the list, overwrite it with the one in the backup
         */
        OVERWRITE,

        /**
         * if there is a series or a comic that is both in the backup and in the list, keep the one in the list
         */
        KEEP,

        /**
         * delete everything in the list and import the backup
         */
        REPLACE,

        /**
         * if there is a series that is both in the backup and in the list, import the series/comic with a different id
         */
        KEEP_BOTH
    }
}

