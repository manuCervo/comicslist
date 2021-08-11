package mcervini.comicslist.dialogs

import mcervini.comicslist.Availability
import mcervini.comicslist.Comic
import mcervini.comicslist.R

class EditComicDialogFragment(private val currentData: Comic, private val onConfirm: (String, Int, Availability, Boolean) -> Unit) : InsertComicDialogFragment(R.string.edit_comic) {

    override fun onDialogConfirmed(number: Int, title: String, availability: Availability) {
        var numberChanged = false
        if (number != currentData.number) {
            numberChanged = true
        }

        if (title != currentData.title || availability != currentData.availability || numberChanged) {
            onConfirm(title, number, availability, numberChanged)
        }
    }

    override fun initializeView() {
        numberEditText.setText(currentData.number.toString())
        titleEditText.setText(currentData.title)
        availabilitySpinner.setSelection(Availability.values().indexOf(currentData.availability))
    }

    override fun checkValidData(): Boolean {
        if (numberEditText.text.isNullOrBlank()) {
            numberEditText.error = getString(R.string.comic_number_cant_be_empty)
            return false
        }
        val number: Int = numberEditText.text.toString().toInt()
        for (c in currentData.series.comics) {
            if (c != currentData && number == c.number) {
                numberEditText.error = getString(R.string.comic_number_already_used)
                return false
            }
        }
        return true
    }
}