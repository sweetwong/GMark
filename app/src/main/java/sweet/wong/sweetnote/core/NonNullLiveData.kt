package sweet.wong.sweetnote.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * A [MutableLiveData] which it's value must not be null, this class is more friendly when interacts with Kotlin
 *
 * @author sweetwang 2021/9/2
 */
open class NonNullLiveData<T>(value: T) : MutableLiveData<T>(value) {

    override fun getValue(): T {
        return super.getValue() ?: throw NullPointerException()
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }

}

fun <T> LiveData<T>.toNonNull() =
    object : NonNullLiveData<T>(this.value ?: throw NullPointerException()) {

        override fun getValue(): T {
            return this@toNonNull.value ?: throw NullPointerException()
        }

        override fun setValue(value: T) {
            toast("This live data is not mutable, please remove set value function")
        }

    }

fun <T> MutableLiveData<T>.toNonNull() =
    object : NonNullLiveData<T>(this.value ?: throw NullPointerException()) {

        override fun getValue(): T {
            return this@toNonNull.value ?: throw NullPointerException()
        }

        override fun setValue(value: T) {
            this@toNonNull.value = value
        }

    }