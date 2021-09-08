package mcervini.comicslist

import android.content.Context
import android.util.AttributeSet
import android.view.ContextMenu
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

/**
 * extension of the recyclerview specialized for holding series with their comics in nested recycler views
 */
class SeriesRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * contains information on what position in the list and what position on the nested list where selected when opening a context menu
     */
    class ContextMenuInfo(
        val position: Int,
        val nestedPosition: Int
    ) : ContextMenu.ContextMenuInfo


    private var contextMenuInfo: ContextMenuInfo? = null


    override fun getContextMenuInfo() = contextMenuInfo

    override fun showContextMenuForChild(originalView: View): Boolean {
        saveContextMenuInfo(originalView)
        return super.showContextMenuForChild(originalView)
    }

    override fun showContextMenuForChild(originalView: View, x: Float, y: Float): Boolean {
        saveContextMenuInfo(originalView)
        return super.showContextMenuForChild(originalView, x, y)
    }

    /**
     * saves the information about what series and wat comic in the series was selected for the context menu
     */
    private fun saveContextMenuInfo(originalView: View) {

        val parent = originalView.parent
        val nestedPosition: Int
        val position: Int

        if (parent != this) {
            //the selected item is not a direct child of this view, which means that a comic was selected
            nestedPosition = getChildAdapterPosition(originalView)
            position = getChildAdapterPosition(parent.parent as LinearLayout)
        } else {
            position = getChildAdapterPosition(originalView)
            nestedPosition = -1
        }
        contextMenuInfo = ContextMenuInfo(position, nestedPosition)
    }
}