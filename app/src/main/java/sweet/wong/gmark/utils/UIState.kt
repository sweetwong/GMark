package sweet.wong.gmark.utils

import androidx.annotation.UiThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

abstract class UIState {

    private val refreshEvent = MutableLiveData<Event<Unit>>()

    @UiThread
    fun bind(owner: LifecycleOwner, action: () -> Unit) {
        action()
        refreshEvent.observe(owner, EventObserver {
            action()
        })
    }

    fun updateUI() {
        refreshEvent.postValue(Event(Unit))
    }

}