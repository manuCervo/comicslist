package mcervini.comicslist.io

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mcervini.comicslist.BackgroundTask
import mcervini.comicslist.R
import mcervini.comicslist.Series
import java.io.OutputStream
import java.util.concurrent.Executor

class AsyncExporter(
    private val series: MutableList<Series>,
    stream: OutputStream,
    activity: AppCompatActivity,
    executor: Executor
) : BackgroundTask(
    activity,
    executor,
    R.string.creating_backup
) {
    private val exporter = JsonExporter(stream)
    override fun doTask() {
        exporter.export(series)
    }

    override fun afterTask() {
        Toast.makeText(
            activity.applicationContext,
            R.string.comics_exported, Toast.LENGTH_SHORT
        )
            .show()
    }
}