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

class EditSeriesDialogFragment(private val series: Series, val onConfirm: (String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activity != null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)

            builder.setTitle(R.string.edit_series)
            val editText: EditText = EditText(activity)
            editText.setHint(R.string.name)
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.maxLines = 1
            editText.setText(series.name)
            builder.setView(editText)
            builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
            builder.setPositiveButton(R.string.ok) { dialog, which ->
                val newName: String = editText.text.toString()
                onConfirm(newName)
                dialog.dismiss()
            }


            val dialog: AlertDialog = builder.create()
            editText.doOnTextChanged { text, start, before, count ->
                val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.isEnabled = !text.isNullOrBlank()
                if (!button.isEnabled) {
                    editText.error = getString(R.string.name_cant_be_empty)
                }
            }

            return dialog

        } else {
            throw IllegalStateException("activity is null")
        }
    }
}