package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.Availability
import mcervini.comicslist.R
import mcervini.comicslist.adapters.AvailabilitySpinnerAdapter

class NewSeriesDialogFragment(private val onConfirm: (String, Int, Availability) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater: LayoutInflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.new_series_dialog, null)
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)

            builder.setTitle(R.string.new_Series)
            builder.setView(view)

            val nameEditText: EditText = view.findViewById(R.id.seriesNameEditText)
            val numberEditText: EditText = view.findViewById(R.id.numberofComicsEditText)
            val availabilitySpinner: Spinner = view.findViewById(R.id.availabilitySpinner)
            availabilitySpinner.adapter = AvailabilitySpinnerAdapter(requireContext())
            availabilitySpinner.setSelection(0)

            builder.setPositiveButton(R.string.ok) { dialog, id ->
                val name: String = nameEditText.text.toString()
                val numberOfComics: Int = numberEditText.text.toString().toIntOrNull() ?: 0
                val availability: Availability = availabilitySpinner.selectedItem as Availability
                onConfirm(name, numberOfComics, availability)
            }

            builder.setNegativeButton(R.string.cancel) { dialog, id ->
                dismiss()
            }

            val dialog = builder.create()

            dialog.setOnShowListener {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                nameEditText.error = getString(R.string.name_cant_be_empty)
            }

            nameEditText.doOnTextChanged { text, start, before, count ->
                val button: Button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.isEnabled = ! text.isNullOrBlank()
                if(!button.isEnabled)
                {
                    nameEditText.error = getString(R.string.name_cant_be_empty)
                }
            }
            dialog
        } ?: throw IllegalStateException("activity is null")
    }
}