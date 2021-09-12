package sweet.wong.gmark.repo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sweet.wong.gmark.core.io
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Page

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