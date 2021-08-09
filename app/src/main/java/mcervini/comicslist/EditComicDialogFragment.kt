package mcervini.comicslist

import android.app.Dialog
import android.os.Bundle

class EditComicDialogFragment(private val currentData: Comic, private val onConfirm: (String, Int, Availability, Boolean) -> Unit) : InsertComicDialogFragment("modifica fumetto") {

    override fun onDialogConfirmed(number: Int, title: String, availability: Availability) {
        var numberChanged = false
        if (number != currentData.number) {
            numberChanged = true
        }

        if (title != currentData.title || availability != currentData.availability || numberChanged) {
            onConfirm(title, number, availability, numberChanged)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        initializeView {
            numberEditText.setText(currentData.number.toString())
            titleEditText.setText(currentData.title)
            availabilitySpinner.setSelection(Availability.values().indexOf(currentData.availability))
        }
        return dialog
    }

    private fun showCurrentData() {
        numberEditText.setText(currentData.number)
        titleEditText.setText(currentData.title)
        availabilitySpinner.setSelection(Availability.values().indexOf(currentData.availability))
    }


    override fun checkValidData(): Boolean {
        if (numberEditText.text.isNullOrBlank()) {
            numberEditText.error = "il numero non èuò essere vuoto"
            return false
        }
        val number: Int = numberEditText.text.toString().toInt()
        for (c in currentData.series.comics) {
            if (c != currentData && number == c.number) {
                numberEditText.error = "c'è già un altro fumetto con questo numero"
                return false
            }
        }

        return true
    }
}