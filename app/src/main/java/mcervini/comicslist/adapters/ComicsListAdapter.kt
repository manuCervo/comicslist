package mcervini.comicslist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import mcervini.comicslist.Comic
import mcervini.comicslist.R

class ComicsListAdapter(private val list: MutableList<Comic>) : RecyclerView.Adapter<ComicsListViewHolder>() {
    private val resource = R.layout.listitem_comic
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComicsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(resource, parent, false)
        return ComicsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ComicsListViewHolder, position: Int) {
        val comic: Comic = list[position]
        holder.titleTextView.text = if (comic.title.isBlank()) {
            comic.series.name
        } else {
            comic.title
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}