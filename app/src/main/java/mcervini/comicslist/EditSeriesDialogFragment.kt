package mcervini.comicslist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment

class EditSeriesDialogFragment(private val series: Series, val onConfirm: (String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activity != null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
            val inflater: LayoutInflater = LayoutInflater.from(activity)

            builder.setTitle("modifica serie")
            val editText: EditText = EditText(activity)
            editText.hint = "nome"
            editText.inputType = InputType.TYPE_CLASS_TEXT
            editText.maxLines = 1
            editText.setText(series.name)
            builder.setView(editText)
            builder.setNegativeButton("annulla") { dialog, which -> dialog.dismiss() }
            builder.setPositiveButton("ok") { dialog, which ->
                val newName: String = editText.text.toString()
                onConfirm(newName)
                dialog.dismiss()
            }


            val dialog: AlertDialog = builder.create()
            editText.doOnTextChanged { text, start, before, count ->
                val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.isEnabled = !text.isNullOrBlank()
                if (!button.isEnabled) {
                    editText.error = "il nome non può essere vuoto"
                }
            }

            return dialog

        } else {
            throw IllegalStateException("activity is null")
        }
    }
}