package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.Availability
import mcervini.comicslist.R
import mcervini.comicslist.adapters.AvailabilitySpinnerAdapter

abstract class InsertComicDialogFragment(@StringRes private val titleRes:Int) : DialogFragment() {

    protected lateinit var numberEditText: EditText
    protected lateinit var titleEditText: EditText
    protected lateinit var availabilitySpinner: Spinner

    protected abstract fun onDialogConfirmed(number: Int, title: String, availability: Availability)
    protected abstract fun checkValidData(): Boolean

    protected abstract fun initializeView()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activity == null) {
            throw IllegalStateException("activity is null")
        }

        val builder = AlertDialog.Builder(activity)

        val inflater: LayoutInflater = LayoutInflater.from(activity)
        val view: View = inflater.inflate(R.layout.insert_comic_dialog, null)

        numberEditText = view.findViewById(R.id.comicNumberEditText)
        titleEditText = view.findViewById(R.id.comicTitleEditText)
        availabilitySpinner = view.findViewById(R.id.availabilitySpinner)
        availabilitySpinner.adapter = AvailabilitySpinnerAdapter(requireContext())

        builder.setTitle(titleRes)
        builder.setView(view)


        builder.setNegativeButton(R.string.cancel) { dialog, which -> dialog.dismiss() }
        builder.setPositiveButton(R.string.ok) { dialog, which ->
            val number: Int = numberEditText.text.toString().toInt()
            val title: String = titleEditText.text.toString()
            val availability: Availability = availabilitySpinner.selectedItem as Availability
            onDialogConfirmed(number, title, availability)
        }

        val dialog = builder.create()

        dialog.setOnShowListener {
            initializeView()
        }

        numberEditText.doOnTextChanged { text, start, before, count ->
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.isEnabled = checkValidData()
        }

        return dialog
    }
}