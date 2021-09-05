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
        val collapse =
            collapsedSeries[series.id] ?: false.also { collapsedSeries[series.id] = false }

        with(holder)
        {
            seriesNameTextView.text = series.name
            comicsRecyclerView.adapter = ComicsListAdapter(series.comics)
            resetForBinding(collapse)

            onCollapseChange = { collapsed ->
                collapsedSeries[series.id] = collapsed
            }
        }
    }

    class SeriesViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        val seriesNameTextView: TextView = view.findViewById(R.id.seriesNameTextView)
        val comicsRecyclerView: RecyclerView = view.findViewById(R.id.comicsRecyclerView)
        private val expandButton: ImageButton = view.findViewById(R.id.expandButton)
        private val animationDuration =
            view.context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()


        var onCollapseChange: (Boolean) -> Unit = {}

        private var collapsed = false


        init {
            view.isLongClickable = true
            expandButton.setOnClickListener {
                collapsed = !collapsed
                onCollapseChange(collapsed)
                val rotation = if (collapsed) {
                    comicsRecyclerView.visibility = GONE
                    -90f
                } else {
                    comicsRecyclerView.visibility = VISIBLE
                    0f
                }
                expandButton.animate()
                    .rotation(rotation)
                    .setDuration(animationDuration)
            }
        }

        fun resetForBinding(collapse: Boolean) {
            expandButton.rotation = if (collapse) {
                comicsRecyclerView.visibility = GONE
                -90f
            } else {
                comicsRecyclerView.visibility = VISIBLE
                0f
            }
            collapsed = collapse
        }

    }
}