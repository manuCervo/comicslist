package mcervini.comicslist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import mcervini.comicslist.Availability
import mcervini.comicslist.R

/**
 * adapter for choosing the availability from a spinner
 */
class AvailabilitySpinnerAdapter(context: Context) : ArrayAdapter<Availability>(
    context,
    R.layout.listitem_availability_spinner,
    Availability.values()
) {
    private val items = Availability.values()
    private val resource = R.layout.listitem_availability_spinner


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val item: Availability = items[position]
        val view: View = convertView ?: LayoutInflater.from(context).inflate(resource, null)
        val textView: TextView = view.findViewById(R.id.availabilitySpinnerTextView)
        val imageView: ImageView = view.findViewById(R.id.availabilitySpinnerImageView)

        textView.text = context.getString(item.stringRes)
        imageView.setColorFilter(context.getColor(item.colorRes))

        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }
}

