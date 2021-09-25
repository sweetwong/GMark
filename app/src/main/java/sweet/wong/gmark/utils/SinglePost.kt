package sweet.wong.gmark.utils

import android.os.Handler
import android.os.Looper

class SinglePost {

    private val handler = Handler(Looper.getMainLooper())

    private var runnable: Runnable? = null

    fun postDelayed(delayMillis: Long, action: () -> Unit) {
        runnable?.let { handler.removeCallbacks(it) }
        val r = Runnable { action() }
        handler.postDelayed(r, delayMillis)
        runnable = r
    }

}