package mcervini.comicslist

import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import mcervini.comicslist.dialogs.ProgressDialog
import java.util.concurrent.Executor

abstract class BackgroundTask(
    protected val activity: AppCompatActivity, private val executor: Executor, @StringRes title: Int
) {
    protected val progressDialog = ProgressDialog(title)
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