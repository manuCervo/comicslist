package mcervini.comicslist.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mcervini.comicslist.R

class ComicsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val titleTextView: TextView = view.findViewById(R.id.comicTitleTextView)
    val numberTextView: TextView = view.findViewById(R.id.comicNumberTextView)
    val availabilityImageView: ImageView = view.findViewById(R.id.availabilityImageView)
}