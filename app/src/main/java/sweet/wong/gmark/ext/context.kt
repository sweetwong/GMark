package sweet.wong.gmark.ext

import android.app.Activity
import android.content.Context
import android.content.Intent


inline fun <reified T : Activity> Context.start(action: Intent.() -> Unit = {}) {
    val intent = Intent(this, T::class.java)
    action(intent)
    startActivity(intent)
}