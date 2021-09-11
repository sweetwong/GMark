package sweet.wong.gmark.repo.drawer.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sweet.wong.gmark.core.io
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.repo.viewmodel.RepoViewModel

class HistoryViewModel : ViewModel() {

    lateinit var repoViewModel: RepoViewModel

    val histories = MutableLiveData<List<Page>>()

    fun refresh() {
        io {
            histories.postValue(DaoManager.pageDao.getAll())
        }
    }

}