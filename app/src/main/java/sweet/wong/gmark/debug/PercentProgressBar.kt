package sweet.wong.gmark.debug

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.ext.getColorFromAttr

class PercentProgressBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var color: Int = App.app.getColorFromAttr(R.attr.colorRipple)

    private var animator: Animator? = null
    private var currentProgress: Float = 0f

    private val paint = Paint().apply {
        color = this@PercentProgressBar.color
        isAntiAlias = true
    }

    fun setProgress(progress: Int, anim: Boolean = true) {
        val newProgress = progress.toFloat()
        if (newProgress < 0 || newProgress > 100) {
            return
        }
        animator?.cancel()

        if (anim) {
            val startProgress = currentProgress
            val diffProgress = newProgress - currentProgress
            animator = ValueAnimator.ofFloat(0f, 1f).apply {
                duration = 200
                interpolator = AccelerateInterpolator()
                addUpdateListener {
                    val animatedValue = it.animatedValue as Float
                    val progressAdd = diffProgress * animatedValue
                    currentProgress = startProgress + progressAdd
                }
            }
            animator?.start()
        } else {
            currentProgress = newProgress
        }

        invalidate()
    }

    fun reset() {
        setProgress(0, false)
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

}