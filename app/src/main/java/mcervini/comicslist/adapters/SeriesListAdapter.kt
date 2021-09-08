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

/**
 * adapter for showing a list of series in a recyclerView
 * each series is displayed with a list of all its comics that can be hidden
 *
 * @param list the list of series
 */
class SeriesListAdapter(private val list: SortedList<Series>) :
    RecyclerView.Adapter<SeriesListAdapter.SeriesViewHolder>() {
    private val resource = R.layout.listitem_series

    //keeps track of which series should be shown with their list of comics hidden.
    //this is because the recyclerView re-uses views that are outside the screen and the onBindViewHolder method needs to know if the view of a series was collapsed or not
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

    /**
     * view holder for the SeriesListAdapter
     */
    class SeriesViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val seriesNameTextView: TextView = view.findViewById(R.id.seriesNameTextView)
        val comicsRecyclerView: RecyclerView = view.findViewById(R.id.seriesRecyclerView)
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

                val rotation: Float
                if (collapsed) {
                    comicsRecyclerView.visibility = GONE
                    rotation = -90f
                } else {
                    comicsRecyclerView.visibility = VISIBLE
                    rotation = 0f
                }

                expandButton.animate()
                    .rotation(rotation)
                    .setDuration(animationDuration)
            }
        }

        /**
         * sets the visibility of the list of comics and the rotation of the button properly
         */
        fun resetForBinding(collapse: Boolean) {
            if (collapse) {
                comicsRecyclerView.visibility = GONE
                expandButton.rotation = -90f
            } else {
                comicsRecyclerView.visibility = VISIBLE
                expandButton.rotation = 0f
            }
            collapsed = collapse
        }
    }
}