package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import mcervini.comicslist.R

class ImportOptionsDialog(private val onConfirm: (Boolean) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater: LayoutInflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.import_options_dialog, null)
        val radioGroup: RadioGroup = view.findViewById(R.id.importOptionsRadioGroup)
        radioGroup.check(R.id.overwriteRadioButton)
        return AlertDialog.Builder(requireActivity())
            .setTitle(R.string.import_backup)
            .setView(view)
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(R.string.ok) { _, _ ->
                onConfirm(
                    when (radioGroup.checkedRadioButtonId) {
                        R.id.keepRadioButton -> false
                        R.id.overwriteRadioButton -> true
                        else -> true
                    }
                )
            }
            .create()
    }
}