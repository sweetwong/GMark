package sweet.wong.sweetnote.core

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

private val handler = Handler(Looper.getMainLooper())

fun runOnMainThread(action: () -> Unit) {
    if (Looper.myLooper() == Looper.getMainLooper()) {
        action()
        return
    }
    handler.post(action)
}

fun postDelayed(delayMillis: Long, action: () -> Unit) {
    handler.postDelayed(action, delayMillis)
}

fun log(vararg any: Any?) {
    Log.d("调试", any.joinToString(", "))
}

fun toast(vararg any: Any?) {
    runOnMainThread {
        Toast.makeText(App.app, any.joinToString(", "), Toast.LENGTH_SHORT).show()
        log(*any)
    }
}

internal inline fun <reified T : Any> noOpDelegate(): T {
    val javaClass = T::class.java
    return Proxy.newProxyInstance(
        javaClass.classLoader, arrayOf(javaClass), NO_OP_HANDLER
    ) as T
}

private val NO_OP_HANDLER = InvocationHandler { _, _, _ ->
    // no op
}