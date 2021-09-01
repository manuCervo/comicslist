package mcervini.comicslist.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import mcervini.comicslist.R
import mcervini.comicslist.Series
import java.util.*

class SeriesListAdapter(private val list: SortedList<Series>) :
    RecyclerView.Adapter<SeriesListAdapter.SeriesViewHolder>() {
    private val resource = R.layout.listitem_series

    private val collapsedSeries = mutableMapOf<UUID, Boolean>()

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

        holder.onCollapseChange = { collapsed ->
            collapsedSeries[series.id] = collapsed
        }

        holder.collapsed = collapsedSeries[series.id] ?: false
        holder.seriesNameTextView.text = series.name
        holder.comicsRecyclerView.adapter = ComicsListAdapter(series.comics)
    }


    class SeriesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val seriesNameTextView: TextView = view.findViewById(R.id.seriesNameTextView)
        val comicsRecyclerView: RecyclerView = view.findViewById(R.id.comicsRecyclerView)
        private val expandButton: ImageButton = view.findViewById(R.id.expandButton)
        private val animationDuration =
            view.context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

        lateinit var onCollapseChange: (Boolean) -> Unit

        var collapsed = false
            set(value) {
                field = value
                onCollapseChange(value)

                comicsRecyclerView.visibility = if (value) {
                    GONE
                } else {
                    VISIBLE
                }

                val rotation = if (value) {
                    -90f
                } else {
                    0f
                }

                expandButton.animate()
                    .rotation(rotation)
                    .setDuration(animationDuration)
                    .start()
            }

        init {
            view.isLongClickable = true
            expandButton.setOnClickListener { collapsed = !collapsed }
        }
    }
}