package sweet.wong.gmark.newrepo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Repo

class NewRepoViewModel : ViewModel() {

    fun addNewRepo(repo: Repo) = liveData(Dispatchers.IO) {
        try {
            DaoManager.repoDao.insertAll(repo)
            emit(Unit)
        } catch (e: Exception) {
            toast("Add new repo failed", e)
        }
    }

}