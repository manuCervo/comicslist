package mcervini.comicslist.adapters

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mcervini.comicslist.R

class SeriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val seriesNameTextView: TextView = view.findViewById(R.id.seriesNameTextView)
    val comicsRecyclerView: RecyclerView = view.findViewById(R.id.comicsRecyclerView)
    private val expandButton: ImageButton = view.findViewById(R.id.expandButton)
    private val collapseImageResource = R.drawable.ic_round_expand_less_24
    private val expandImageResource = R.drawable.ic_round_expand_more_24

    init {
        expandButton.setOnClickListener {

            comicsRecyclerView.visibility = if (comicsRecyclerView.visibility == VISIBLE) {
                expandButton.setImageResource(expandImageResource)
                GONE
            } else {
                expandButton.setImageResource(collapseImageResource)
                VISIBLE
            }
        }
    }
}