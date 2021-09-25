package sweet.wong.gmark.utils

import androidx.lifecycle.MutableLiveData

abstract class UIState {

    val refreshEvent = MutableLiveData<Event<Unit>>()

}