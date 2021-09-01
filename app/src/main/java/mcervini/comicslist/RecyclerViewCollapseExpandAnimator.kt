package mcervini.comicslist

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewCollapseExpandAnimator(
    private val recyclerView: RecyclerView,
    private val animationDuration: Long
) {

    private var expandedSize = recyclerView.measuredHeight
    fun playCollapse() {
        expandedSize = recyclerView.measuredHeight

        val slideUpAnimation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    recyclerView.visibility = View.GONE
                } else {
                    recyclerView.layoutParams.height =
                        (expandedSize.toFloat() * (1f - interpolatedTime)).toInt()
                    recyclerView.requestLayout()
                }
            }
        }.apply { duration = animationDuration }


        recyclerView.startAnimation(slideUpAnimation)
    }

    fun playExpand() {
        recyclerView.visibility = View.VISIBLE
        val targetHeight = expandedSize

        val slideUpAnimation: Animation = object : Animation() {

            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                recyclerView.layoutParams.height = if (interpolatedTime == 1f) {
                    ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    (targetHeight.toFloat() * interpolatedTime).toInt()
                }
                recyclerView.requestLayout()
            }
        }.apply { duration = animationDuration }


        recyclerView.startAnimation(slideUpAnimation)
    }
}