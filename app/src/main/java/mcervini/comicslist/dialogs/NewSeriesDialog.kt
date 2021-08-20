package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.Availability
import mcervini.comicslist.R
import mcervini.comicslist.adapters.AvailabilitySpinnerAdapter

class NewSeriesDialog(private val onConfirm: (String, Int, Availability) -> Unit) :
    DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val view: View = it.layoutInflater.inflate(R.layout.new_series_dialog, null)

            val nameEditText: EditText = view.findViewById(R.id.seriesNameEditText)
            val numberEditText: EditText = view.findViewById(R.id.numberofComicsEditText)

            val availabilitySpinner: Spinner =
                view.findViewById<Spinner>(R.id.availabilitySpinner).apply {
                    adapter = AvailabilitySpinnerAdapter(requireContext())
                    setSelection(0)
                }

            val dialog = AlertDialog.Builder(it).setTitle(R.string.new_Series)
                .setView(view)
                .setPositiveButton(R.string.ok) { _, _ ->
                    val name: String = nameEditText.text.toString()
                    val numberOfComics: Int = numberEditText.text.toString().toIntOrNull() ?: 0
                    val availability: Availability =
                        availabilitySpinner.selectedItem as Availability
                    onConfirm(name, numberOfComics, availability)
                }
                .setNegativeButton(R.string.cancel) { _, _ ->
                    dismiss()
                }.create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                nameEditText.error = getString(R.string.name_cant_be_empty)
            }


            nameEditText.doOnTextChanged { text, _, _, _ ->
                val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.isEnabled = !text.isNullOrBlank()
                if (!button.isEnabled) {
                    nameEditText.error = getString(R.string.name_cant_be_empty)
                }
            }

            dialog
        } ?: throw IllegalStateException("activity is null")
    }
}