package mcervini.comicslist.io.backup

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import mcervini.comicslist.BackgroundTask
import mcervini.comicslist.Comic
import mcervini.comicslist.R
import mcervini.comicslist.Series
import mcervini.comicslist.adapters.SeriesListAdapter
import mcervini.comicslist.io.ComicsDAO
import mcervini.comicslist.io.SeriesDAO
import java.io.IOException
import java.util.*
import java.util.concurrent.Executor

class AsyncImporter(
    private val importer: Importer,
    activity: AppCompatActivity,
    private val list: MutableList<Series>,
    private val seriesDAO: SeriesDAO,
    private val comicsDAO: ComicsDAO,
    private val adapter: SeriesListAdapter,
    executor: Executor,
    private val overwriteExisting: Boolean = true
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

        val currentSeries = mutableMapOf<UUID, Series>()
        val currentComics = mutableMapOf<Pair<UUID, Int>, Comic>()

        for (s in list) {
            currentSeries[s.id] = s
            for (c in s.comics) {
                currentComics[Pair(s.id, c.number)] = c
            }
        }

        activity.runOnUiThread {
            progressDialog.setMax(imported.size)
            progressDialog.setIndeterminate(false)
        }

        for ((progress, s) in imported.withIndex()) {
            val seriesId: UUID = s.id
            val series: Series? = currentSeries[seriesId]

            if (series != null) {
                if (overwriteExisting && series != s) {
                    series.name = s.name
                    seriesDAO.updateSeries(series)
                }

                for (c in s.comics) {
                    val key = Pair(seriesId, c.number)
                    val comic: Comic? = currentComics[key]

                    if (comic != null) {
                        if (overwriteExisting) {
                            comic.title = c.title
                            comic.availability = c.availability
                            comicsDAO.updateComic(comic)
                        }
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

            activity.runOnUiThread {
                progressDialog.setProgress(progress)
            }
        }
        activity.runOnUiThread { adapter.notifyDataSetChanged() }
    }
}

