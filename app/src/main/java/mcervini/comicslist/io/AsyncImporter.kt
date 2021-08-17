package mcervini.comicslist.io

import androidx.appcompat.app.AppCompatActivity
import mcervini.comicslist.BackgroundTask
import mcervini.comicslist.Comic
import mcervini.comicslist.R
import mcervini.comicslist.Series
import mcervini.comicslist.adapters.SeriesListAdapter
import java.io.InputStream
import java.util.*
import java.util.concurrent.Executor

class AsyncImporter(
    activity: AppCompatActivity,
    private val stream: InputStream,
    private val list: MutableList<Series>,
    private val seriesDAO: SeriesDAO,
    private val comicsDAO: ComicsDAO,
    private val adapter: SeriesListAdapter,
    executor: Executor
) : BackgroundTask(
    activity, executor,
    R.string.importing_backup
) {
    override fun doTask() {
        val imported: MutableList<Series> = JsonImporter(stream).import()

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
            if (currentSeries.containsKey(seriesId)) {
                val series: Series = currentSeries[seriesId]!!
                if (series != s) {
                    series.name = s.name
                    seriesDAO.updateSeries(series)
                }

                for (c in s.comics) {
                    val key = Pair(seriesId, c.number)
                    if (currentComics.containsKey(key)) {
                        val comic: Comic = currentComics[key]!!
                        if (comic != c) {
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
    }

    override fun afterTask() {
        adapter.notifyDataSetChanged()
    }
}