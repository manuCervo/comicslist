package mcervini.comicslist.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mcervini.comicslist.Comic
import mcervini.comicslist.R

/**
 * adapter for showing a list of comics in a recyclerView
 *
 * @param list the list of comics
 */
class ComicsListAdapter(private val list: MutableList<Comic>) :
    RecyclerView.Adapter<ComicsListAdapter.ComicsListViewHolder>() {

    private val resource = R.layout.listitem_comic
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicsListViewHolder {
        return ComicsListViewHolder(
            LayoutInflater.from(parent.context).inflate(resource, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ComicsListViewHolder, position: Int) {
        val comic: Comic = list[position]

        holder.apply {
            val context: Context = holder.itemView.context

            titleTextView.text = if (comic.title.isBlank()) {
                comic.series.name
            } else {
                comic.title
            }

            numberTextView.text = "${comic.number}"
            availabilityImageView.setColorFilter(context.getColor(comic.availability.colorRes))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * view holder for the ComicsListAdapter
     */
    class ComicsListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            view.isLongClickable = true
        }

        val titleTextView: TextView = view.findViewById(R.id.comicTitleTextView)
        val numberTextView: TextView = view.findViewById(R.id.comicNumberTextView)
        val availabilityImageView: ImageView = view.findViewById(R.id.availabilityImageView)
    }
}

