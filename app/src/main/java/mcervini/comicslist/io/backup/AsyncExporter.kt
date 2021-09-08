package mcervini.comicslist.io.backup

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import mcervini.comicslist.BackgroundTask
import mcervini.comicslist.R
import mcervini.comicslist.Series
import java.io.IOException
import java.util.concurrent.Executor

/**
 * asynchronously exports a list of series, while showing a ProgressDialog
 *
 * @param series the list of series to be exported
 * @param exporter the exporter to use for exporting
 * @param executor an Executor used for running the exporting process in a background thread
 */
class AsyncExporter(
    private val series: MutableList<Series>,
    private val exporter: Exporter,
    activity: AppCompatActivity,
    executor: Executor
) : BackgroundTask(
    activity,
    executor,
    R.string.creating_backup
) {
    override fun doTask() {
        try {
            exporter.export(series)
        } catch (e: IOException) {
            activity.runOnUiThread {
                Toast.makeText(
                    activity.applicationContext,
                    R.string.cant_write_file,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        activity.runOnUiThread {
            Toast.makeText(
                activity.applicationContext,
                R.string.export_complete, Toast.LENGTH_SHORT
            ).show()
        }
    }
}