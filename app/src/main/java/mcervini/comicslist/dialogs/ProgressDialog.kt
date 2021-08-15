package mcervini.comicslist.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import mcervini.comicslist.R

class ProgressDialog(@StringRes private val title: Int) : DialogFragment() {
    private lateinit var progressBar: ProgressBar
    private var showing = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        if (activity == null) {
            throw IllegalStateException("activity is null")
        }
        val inflater: LayoutInflater = LayoutInflater.from(requireContext())
        val view: View = inflater.inflate(R.layout.progress_dialog, null)
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.isIndeterminate = true

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(false)
            .setTitle(title)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }


    fun setProgress(progress: Int) {
        if (showing) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                progressBar.setProgress(progress, true)
            } else {
                progressBar.progress = progress
            }
        }
    }

    fun setMax(max: Int) {
        if (showing) {
            progressBar.max = max
        }
    }

    fun setIndeterminate(indeterminate: Boolean) {
        if (showing) {
            progressBar.isIndeterminate = indeterminate
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        showing = true
        super.show(manager, tag)
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        showing = true
        return super.show(transaction, tag)
    }

    override fun dismiss() {
        showing = false
        super.dismiss()
    }
}