package sweet.wong.sweetnote

import android.os.Handler
import android.os.Looper

private val handler = Handler(Looper.getMainLooper())

fun runOnMainThread(action: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        action()
        return
    }
    handler.post(action)
}
