package sweet.wong.gmark.ext

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import sweet.wong.gmark.utils.NonNullLiveData

fun MutableLiveData<*>.notify() {
    value = value
}

fun <T> NonNullLiveData<T>.update(action: T.() -> Unit) {
    value.action()
    if (Looper.getMainLooper() == Looper.myLooper()) {
        value = value
    } else {
        postValue(value)
    }
}