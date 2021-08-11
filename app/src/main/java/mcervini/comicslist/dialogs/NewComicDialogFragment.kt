package mcervini.comicslist.dialogs

import mcervini.comicslist.Availability
import mcervini.comicslist.R
import mcervini.comicslist.Series

class NewComicDialogFragment(private val series: Series, private val onConfirm: (Int, String, Availability) -> Unit) : InsertComicDialogFragment(R.string.new_comic) {

    override fun onDialogConfirmed(number: Int, title: String, availability: Availability) {
        onConfirm(number, title, availability)
    }

    override fun initializeView() {
        var nextNumber: Int = 0
        for (c in series.comics) {
            if (c.number > nextNumber) {
                nextNumber = c.number
            }
        }
        nextNumber++
        numberEditText.setText(nextNumber.toString())
    }

    override fun checkValidData(): Boolean {
        if (numberEditText.text.isNullOrBlank()) {
            numberEditText.error = getString(R.string.comic_number_cant_be_empty)
            return false
        }

        val number: Int = numberEditText.text.toString().toInt()
        for (c in series.comics) {
            if (c.number == number) {
                numberEditText.error = getString(R.string.comic_number_already_used)
                return false
            }
        }
        return true
    }
}