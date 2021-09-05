package sweet.wong.gmark.core

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executors

private val handler = Handler(Looper.getMainLooper())

private val executors = Executors.newFixedThreadPool(5)

fun io(action: () -> Unit) {
    executors.execute(action)
}

fun ui(action: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        return action()
    }
    handler.post(action)
}

fun delay(delayMillis: Long, action: () -> Unit) {
    handler.postDelayed(action, delayMillis)
}