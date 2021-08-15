package mcervini.comicslist

import android.content.Context
import android.util.AttributeSet
import android.view.ContextMenu
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView

class SeriesRecyclerView : RecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

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

    private fun saveContextMenuInfo(originalView: View) {

        val parent = originalView.parent
        val nestedPosition: Int
        val position: Int
        if (parent != this) {
            nestedPosition = getChildAdapterPosition(originalView)
            position = getChildAdapterPosition(parent.parent as LinearLayout)
        } else {
            position = getChildAdapterPosition(originalView)
            nestedPosition = -1
        }
        contextMenuInfo = ContextMenuInfo(position, nestedPosition)
    }
}