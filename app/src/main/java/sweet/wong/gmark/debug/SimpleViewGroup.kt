package sweet.wong.gmark.debug

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.core.view.forEach
import kotlin.math.max

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/11/4
 */
class SimpleViewGroup @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var childMaxWidth = 0
        var childMaxHeight = 0
        forEach { child ->
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            childMaxWidth = max(childMaxWidth, child.measuredWidth)
            childMaxHeight = max(childMaxHeight, child.measuredHeight)
        }

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        setMeasuredDimension(
            getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
            getDefaultSize(suggestedMinimumHeight, heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        forEach { child ->
            child.layout(0, 0, child.measuredWidth, child.measuredHeight)
        }
    }

}