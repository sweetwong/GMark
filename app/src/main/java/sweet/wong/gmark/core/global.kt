package sweet.wong.gmark.core

import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

fun log(vararg any: Any?) {
    Log.d("调试", any.joinToString(", "))
}

fun toast(vararg any: Any?) {
    ui {
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

val resources: Resources = App.app.resources