package sweet.wong.gmark.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.res.use
import androidx.databinding.BindingAdapter
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.ext.getColorFromAttr

class PercentProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var defaultColor: Int = App.app.getColorFromAttr(R.attr.colorRipple)

    private var animator: Animator? = null
    private var currentProgress: Float = 0f
    private var lastSetProgressTime: Long = 0

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        context.obtainStyledAttributes(attrs, R.styleable.PercentProgressBar).use { a ->
            val color = a.getColor(R.styleable.PercentProgressBar_color, 0)
            if (color != 0) {
                paint.color = color
            } else {
                paint.color = defaultColor
            }
        }
    }

    fun setProgress(progress: Int, anim: Boolean = true) {
        val newProgress = progress.toFloat()
        if (newProgress < 0 || newProgress > 100) {
            return
        }
        animator?.cancel()

        val currentTime = SystemClock.uptimeMillis()

        if (anim && progress > currentProgress && currentTime - lastSetProgressTime > 33) {
            val startProgress = currentProgress
            val diffProgress = newProgress - currentProgress
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 100
                interpolator = LinearInterpolator()
                addUpdateListener {
                    val animatedValue = it.animatedValue as Float
                    val progressAdd = diffProgress * animatedValue
                    currentProgress = startProgress + progressAdd
                    invalidate()
                }
            }
            animator?.start()
        } else {
            currentProgress = newProgress
            invalidate()
        }

        lastSetProgressTime = currentTime
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(
            0f,
            0f,
            (currentProgress / 100f) * width.toFloat(),
            height.toFloat(),
            paint
        )
    }

    companion object {

        @JvmStatic
        @BindingAdapter("progress")
        fun PercentProgressBar.progress(progress: Int) {
            setProgress(progress)
        }

    }

}