package mcervini.comicslist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import mcervini.comicslist.R
import mcervini.comicslist.Series

class SeriesListAdapter(private val list: SortedList<Series>) :
    RecyclerView.Adapter<SeriesListAdapter.SeriesViewHolder>() {
    private val resource = R.layout.listitem_series

    override fun getItemCount(): Int {
        return list.size()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        return SeriesViewHolder(
            LayoutInflater.from(parent.context).inflate(resource, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val series: Series = list[position]
        holder.seriesNameTextView.text = series.name
        holder.comicsRecyclerView.adapter = ComicsListAdapter(series.comics)

    }


    class SeriesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val seriesNameTextView: TextView = view.findViewById(R.id.seriesNameTextView)
        val comicsRecyclerView: RecyclerView = view.findViewById(R.id.comicsRecyclerView)
        private val expandButton: ImageButton = view.findViewById(R.id.expandButton)
        private val collapseImageResource = R.drawable.ic_round_expand_less_24
        private val expandImageResource = R.drawable.ic_round_expand_more_24

        init {
            view.isLongClickable = true
            expandButton.setOnClickListener {

                comicsRecyclerView.visibility = if (comicsRecyclerView.visibility == View.VISIBLE) {
                    expandButton.setImageResource(expandImageResource)
                    View.GONE
                } else {
                    expandButton.setImageResource(collapseImageResource)
                    View.VISIBLE
                }
            }
        }
    }
}