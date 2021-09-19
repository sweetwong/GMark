package sweet.wong.gmark.newrepo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sweet.wong.gmark.core.io
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.utils.Event

class NewRepoViewModel : ViewModel() {

    fun addNewRepo(repo: Repo): MutableLiveData<Event<Unit>> {
        val successEvent = MutableLiveData<Event<Unit>>()
        io {
            try {
                DaoManager.repoDao.insertAll(repo)
                successEvent.postValue(Event(Unit))
            } catch (e: Exception) {
                toast("Add new repo failed", e)
            }
        }
        return successEvent
    }

}