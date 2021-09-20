package sweet.wong.gmark.repo.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sweet.wong.gmark.core.io
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.repo.RepoViewModel

class HistoryViewModel : ViewModel() {

    lateinit var repoViewModel: RepoViewModel

    val histories = MutableLiveData<List<Page>>()

    fun refresh() {
        io {
            val data = DaoManager.getPageDao(repoViewModel.repo).getAll()
            histories.postValue(data.reversed())
        }
    }

}