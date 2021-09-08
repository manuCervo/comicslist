package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.R
import mcervini.comicslist.Series

/**
 * dialog for editing a series
 *
 * @param series the series to edit
 * @param onConfirm the callback that is called when the user confirms the modifications. the argument is the series's name entered by the user
 */
class EditSeriesDialog(private val series: Series, val onConfirm: (String) -> Unit) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val view: View = it.layoutInflater.inflate(R.layout.edit_series_dialog, null)
            val editText: EditText = view.findViewById(R.id.seriesNameEditText)
            editText.setText(series.name)

            val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.edit_series)
                .setView(view)
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.ok) { dialog, _ ->
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