package sweet.wong.gmark.newrepo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.ext.IO_CATCH

class NewRepoViewModel : ViewModel() {

    fun addNewRepo(repo: Repo) = liveData(Dispatchers.IO_CATCH) {
        DaoManager.repoDao.insertAll(repo)
        emit(Unit)
    }

}