package mcervini.comicslist.dialogs

import android.app.Dialog
import android.os.Bundle
import mcervini.comicslist.Availability
import mcervini.comicslist.Series

class NewComicDialogFragment(private val series: Series, private val onConfirm: (Int, String, Availability) -> Unit) : InsertComicDialogFragment("nuovo fumetto") {

    override fun onDialogConfirmed(number: Int, title: String, availability: Availability) {
        onConfirm(number, title, availability)
    }

    override fun initializeView()
    {
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
            numberEditText.error = "il numero non può essere vuoto"
            return false
        }

        val number: Int = numberEditText.text.toString().toInt()
        for (c in series.comics) {
            if (c.number == number) {
                numberEditText.error = "c'è già un fumetto con questo numero"
                return false
            }
        }
        return true
    }
}