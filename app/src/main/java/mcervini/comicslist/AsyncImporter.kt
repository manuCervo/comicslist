package mcervini.comicslist

import androidx.appcompat.app.AppCompatActivity
import mcervini.comicslist.adapters.SeriesListAdapter
import mcervini.comicslist.io.ComicsDAO
import mcervini.comicslist.io.JsonImporter
import mcervini.comicslist.io.SeriesDAO
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
) : BackgroundTask(activity, executor, R.string.importing_backup) {
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
                list.remove(currentSeries[seriesId])
                list.add(s)
                seriesDAO.updateSeries(s)
            } else {
                seriesDAO.addExistingSeries(s)
                list.add(s)
            }

            for (c in s.comics) {
                val key = Pair(seriesId, c.number)
                if (currentComics.containsKey(key)) {
                    comicsDAO.updateComic(c)
                } else {
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