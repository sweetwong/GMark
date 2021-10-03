package sweet.wong.gmark.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/7
 */
class SnappingLinearLayoutManager(val context: Context) : LinearLayoutManager(context) {

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        val smoothScroller = object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int = SNAP_TO_START

            override fun calculateTimeForScrolling(dx: Int): Int = 100

            override fun calculateTimeForDeceleration(dx: Int): Int = 100
        }
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

}