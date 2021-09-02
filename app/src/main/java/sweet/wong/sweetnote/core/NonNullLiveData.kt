package sweet.wong.sweetnote.core

import androidx.lifecycle.MutableLiveData

/**
 * A [MutableLiveData] which it's value must not be null, this class is more friendly when interacts with Kotlin
 *
 * @author sweetwang 2021/9/2
 */
class NonNullLiveData<T>(value: T) : MutableLiveData<T>(value) {

    override fun getValue(): T {
        return super.getValue() ?: throw NullPointerException()
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }

}