package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.Availability
import mcervini.comicslist.R
import mcervini.comicslist.adapters.AvailabilitySpinnerAdapter

/**
 * base class for dialogs where the user needs to insert or modify a comic
 *
 * @param titleRes the string resource of the title
 */
abstract class InsertComicDialog(@StringRes private val titleRes: Int) : DialogFragment() {

    protected lateinit var numberEditText: EditText
    protected lateinit var titleEditText: EditText
    protected lateinit var availabilitySpinner: Spinner

    /**
     * called when the user confirms the dialog
     *
     * @param number the number that the user entered
     * @param title the tile of the comic that the user entered
     * @param availability the availability of the comic that the user entered
     */
    protected abstract fun onDialogConfirmed(number: Int, title: String, availability: Availability)

    /**
     * returns true if the inserted data is valid
     */
    protected abstract fun checkValidData(): Boolean

    /**
     * initializes the views in the dialog
     */
    protected abstract fun initializeView()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val view: View = it.layoutInflater.inflate(R.layout.insert_comic_dialog, null)

            numberEditText = view.findViewById(R.id.comicNumberEditText)
            titleEditText = view.findViewById(R.id.comicTitleEditText)
            availabilitySpinner = view.findViewById(R.id.availabilitySpinner)
            availabilitySpinner.adapter = AvailabilitySpinnerAdapter(requireContext())


            val dialog = AlertDialog.Builder(it)
                .setTitle(titleRes)
                .setView(view)
                .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(R.string.ok) { _, _ ->
                    val number: Int = numberEditText.text.toString().toInt()
                    val title: String = titleEditText.text.toString()
                    val availability: Availability =
                        availabilitySpinner.selectedItem as Availability
                    onDialogConfirmed(number, title, availability)
                }
                .create()

            dialog.setOnShowListener {
                initializeView()
            }

            numberEditText.doOnTextChanged { _, _, _, _ ->
                val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                button.isEnabled = checkValidData()
            }
            dialog
        } ?: throw IllegalStateException("activity is null")
    }
}