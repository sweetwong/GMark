package sweet.wong.gmark.ext

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import sweet.wong.gmark.utils.NonNullLiveData

fun <T> MutableLiveData<T>.notify() {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        value = value
    } else {
        postValue(value)
    }
}

fun <T> NonNullLiveData<T>.update(action: T.() -> Unit) {
    value.action()
    notify()
}