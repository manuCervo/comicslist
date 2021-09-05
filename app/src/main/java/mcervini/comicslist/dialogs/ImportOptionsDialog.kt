package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.R
import mcervini.comicslist.io.backup.AsyncImporter

class ImportOptionsDialog(private val onConfirm: (AsyncImporter.ImportMode) -> Unit) :
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val view = it.layoutInflater.inflate(R.layout.import_options_dialog, null)
            val radioGroup: RadioGroup =
                view.findViewById<RadioGroup>(R.id.importOptionsRadioGroup).apply {
                    check(R.id.overwriteRadioButton)
                }

            AlertDialog.Builder(requireActivity())
                .setTitle(R.string.import_backup)
                .setView(view)
                .setMessage(R.string.import_options_info)
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.ok) { _, _ ->
                    onConfirm(
                        when (radioGroup.checkedRadioButtonId) {
                            R.id.keepRadioButton -> AsyncImporter.ImportMode.KEEP
                            R.id.overwriteRadioButton -> AsyncImporter.ImportMode.OVERWRITE
                            R.id.ReplaceRadioButton -> AsyncImporter.ImportMode.REPLACE
                            R.id.keepBothRadioButton -> AsyncImporter.ImportMode.KEEP_BOTH
                            else -> throw IllegalStateException()
                        }
                    )
                }
                .create()
        } ?: throw IllegalStateException("activity is null")
    }
}