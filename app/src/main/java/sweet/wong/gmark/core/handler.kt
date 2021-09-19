package sweet.wong.gmark.core

import android.os.Handler
import android.os.Looper

private val handler = Handler(Looper.getMainLooper())

fun ui(action: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        return action()
    }
    handler.post(action)
}

fun delay(delayMillis: Long, action: () -> Unit) {
    handler.postDelayed(action, delayMillis)
}