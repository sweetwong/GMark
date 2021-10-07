package sweet.wong.gmark.webview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import kotlin.math.abs

class MyWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs) {

    private var newY = 0f
    private var newX = 0f
    private var firstMove = false
    private var disallowParentScroll = false
    private var clampedX = false

    /**
     * For a sliding event, only respond to one direction of sliding
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
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
                if (firstMove && (moveY > moveX)) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    disallowParentScroll = true
                }
                if (!disallowParentScroll) {
                    parent.requestDisallowInterceptTouchEvent(!clampedX)
                }
                firstMove = false
            }
            else -> {
                parent.requestDisallowInterceptTouchEvent(false)
                disallowParentScroll = false
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        this.clampedX = clampedX
    }

}