package mcervini.comicslist.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.R

class DeleteDialog(@StringRes private val title: Int, private val onConfirm: () -> Unit) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.delete))
                .setMessage(title)
                .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.yes) { _, _ -> onConfirm() }
                .create()
        } ?: throw IllegalStateException("activity is null")
    }
}