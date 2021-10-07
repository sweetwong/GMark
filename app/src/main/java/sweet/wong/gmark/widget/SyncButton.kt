package sweet.wong.gmark.widget

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.appcompat.widget.AppCompatImageView
import sweet.wong.gmark.R

class SyncButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatImageView(context, attrs) {

    private var isAnimating = false

    init {
        setImageResource(R.drawable.sync)
    }

    fun start() {
        if (isAnimating) {
            return
        }

        val animation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.duration = 2000
        startAnimation(animation)

        isAnimating = true
    }

    fun stop() {
        isAnimating = false
        animation?.cancel()
    }

}