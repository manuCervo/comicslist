package mcervini.comicslist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.adapters.AvailabilitySpinnerAdapter

class NewSeriesDialogFragment(private val onConfirm: (String, Int, Availability) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater: LayoutInflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.new_series_dialog, null)
            val builder: AlertDialog.Builder = AlertDialog.Builder(it)

            builder.setView(view)

            val nameEditText: EditText = view.findViewById(R.id.seriesNameEditText)
            val numberEditText: EditText = view.findViewById(R.id.numberofComicsEditText)
            val availabilitySpinner: Spinner = view.findViewById(R.id.availabilitySpinner)
            availabilitySpinner.adapter = AvailabilitySpinnerAdapter(requireContext())
            availabilitySpinner.setSelection(0)

            builder.setPositiveButton("ok") { dialog, id ->
                val name: String = nameEditText.text.toString()
                val numberOfComics: Int = numberEditText.text.toString().toIntOrNull() ?: 0
                val availability: Availability = availabilitySpinner.selectedItem as Availability
                onConfirm(name, numberOfComics, availability)
            }

            builder.setNegativeButton("annulla") { dialog, id ->
                dismiss()
            }

            builder.create()
        } ?: throw IllegalStateException("activity is null")
    }
}