package mcervini.comicslist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mcervini.comicslist.R
import mcervini.comicslist.Series

class SeriesListAdapter(private val list: MutableList<Series>) : RecyclerView.Adapter<SeriesViewHolder>() {
    private val resource = R.layout.listitem_series

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)
        return SeriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeriesViewHolder, position: Int) {
        val series: Series = list[position]
        holder.seriesNameTextView.text = series.name
        holder.comicsRecyclerView.adapter = ComicsListAdapter(series.comics)

    }
}