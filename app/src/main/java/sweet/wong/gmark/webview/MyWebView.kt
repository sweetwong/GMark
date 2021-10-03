package sweet.wong.gmark.webview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.math.abs

class MyWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : WebView(context, attrs), LifecycleObserver {

    private var newY = 0f
    private var newX = 0f
    private var firstMove = false

    init {
        (context as AppCompatActivity).lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_PAUSE -> onPause()
            else -> {
            }
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        if (isShown) {
            onResume()
        } else {
            onPause()
        }
        super.onVisibilityChanged(changedView, visibility)
    }

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
            }
            MotionEvent.ACTION_MOVE -> {
                val oldX = newX
                val oldY = newY
                newX = event.x
                newY = event.y
                val moveX = abs(newX - oldX)
                val moveY = abs(newY - oldY)
                if (firstMove && moveY > moveX) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
                firstMove = false
            }
            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.onTouchEvent(event)
    }

}