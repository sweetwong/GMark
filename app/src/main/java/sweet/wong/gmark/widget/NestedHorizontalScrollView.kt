package sweet.wong.gmark.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import kotlin.math.abs

class NestedHorizontalScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : HorizontalScrollView(context, attrs) {

    private var newY = 0f
    private var newX = 0f
    private var firstMove = false
    private var clampedX = false
    private var isHorizontal = false

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                newX = event.x
                newY = event.y
                firstMove = true
                clampedX = false
            }
            MotionEvent.ACTION_MOVE -> {
                val oldX = newX
                val oldY = newY
                newX = event.x
                newY = event.y
                val moveX = abs(newX - oldX)
                val moveY = abs(newY - oldY)
                if (firstMove) {
                    isHorizontal = moveY < moveX
                    firstMove = false
                }

                if (isHorizontal) {
                    parent.requestDisallowInterceptTouchEvent(!clampedX)
                }
            }
            else -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        this.clampedX = clampedX
    }

}