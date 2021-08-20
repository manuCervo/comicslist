package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.R
import mcervini.comicslist.Series

class EditSeriesDialog(private val series: Series, val onConfirm: (String) -> Unit) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val editText: EditText = EditText(activity).apply {
                setHint(R.string.name)
                inputType = InputType.TYPE_CLASS_TEXT
                maxLines = 1
                setText(series.name)
            }

            val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.edit_series)
                .setView(editText)
                .setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
                .setPositiveButton(R.string.ok) { dialog, which ->
                    val newName: String = editText.text.toString()
                    onConfirm(newName)
                    dialog.dismiss()
                }
                .create()


            editText.doOnTextChanged { text, _, _, _ ->
                val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.isEnabled = !text.isNullOrBlank()
                if (!button.isEnabled) {
                    editText.error = getString(R.string.name_cant_be_empty)
                }
            }

            dialog
        } ?: throw IllegalStateException("activity is null")
    }
}