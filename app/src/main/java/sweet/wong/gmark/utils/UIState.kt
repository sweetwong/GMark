package sweet.wong.gmark.utils

import androidx.annotation.UiThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

abstract class UIState<T : UIState<T>> {

    private val refreshEvent = MutableLiveData<Event<Unit>>()

    @UiThread
    fun bind(owner: LifecycleOwner, action: () -> Unit) {
        action()
        refreshEvent.observe(owner, EventObserver {
            action()
        })
    }

    fun updateUI(action: (T.() -> Unit)? = null) {
        action?.let { (this as T).it() }

        refreshEvent.postValue(Event(Unit))
    }

}