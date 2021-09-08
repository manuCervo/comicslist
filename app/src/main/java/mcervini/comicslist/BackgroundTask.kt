package mcervini.comicslist

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import mcervini.comicslist.dialogs.ProgressDialog
import java.util.concurrent.Executor

/**
 * runs the doTask method in a background thread, while showing a progress dialog
 * @param activity the activity from where this background task was executed
 * @param executor an executor used to run the doTask method in background
 * @param title the tile of the progress dialog
 */
abstract class BackgroundTask(
    protected val activity: AppCompatActivity, private val executor: Executor, @StringRes title: Int
) {
    protected val progressDialog = ProgressDialog(title)

    /**
     * shows the progress dialog, then runs doTask in background
     */
    fun run() {
        progressDialog.show(activity.supportFragmentManager, "backgroundTask")
        executor.execute {
            doTask()
            activity.runOnUiThread {
                progressDialog.dismiss()
            }
        }
    }

    protected abstract fun doTask()
}