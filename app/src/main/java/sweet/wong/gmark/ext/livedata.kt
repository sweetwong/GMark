package sweet.wong.gmark.ext

import androidx.lifecycle.MutableLiveData

fun MutableLiveData<*>.notify() {
    value = value
}