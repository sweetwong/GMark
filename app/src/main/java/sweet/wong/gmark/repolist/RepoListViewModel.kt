package sweet.wong.gmark.repolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.core.*
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.git.Clone
import sweet.wong.gmark.utils.Event
import sweet.wong.gmark.utils.NonNullLiveData

/**
 * Git repository list view model
 */
class RepoListViewModel : ViewModel() {

    val repoUIStates = NonNullLiveData<MutableList<RepoUIState>>(mutableListOf())

    val repoSelectEvent = MutableLiveData<Event<Repo>>()

    private fun clone(uiState: RepoUIState) {
        // TODO: 2021/9/3 添加进度条
        io {
            Clone.clone(uiState.repo, object : Clone.CloneCallback by noOpDelegate() {

                override fun onProgress(title: String?, percent: Int) {
                    uiState.repo.state = Repo.STATE_SYNCING
                    ui {
                        log("Git clone onProgress", "title", title, "percent", percent)
                        uiState.statusText = "$title ... ($percent%)"
                        uiState.progress = percent.toString()
                        uiState.refresh?.invoke()
                    }
                }

                override fun onSuccess() {
                    uiState.repo.state = Repo.STATE_SUCCESS
                    DaoManager.repoDao.update(uiState.repo)
                    ui {
                        uiState.statusText = TimeUtils.date2String(TimeUtils.getNowDate())
                        uiState.refresh?.invoke()
                    }
                }

                override fun onFailed(e: Exception) {
                    uiState.repo.state = Repo.STATE_FAILED
                    DaoManager.repoDao.update(uiState.repo)
                }

            })
        }
    }

    fun refreshRepoList() {
        // FIXME: 2021/9/2 这里刷新可能会导致丢失UI状态
        viewModelScope.launch {
            try {
                val repos = withContext(Dispatchers.IO) { DaoManager.repoDao.getAll() }
                val repoUtiStatesValue = mutableListOf<RepoUIState>()
                repos.forEach { repo ->
                    val uiState = RepoUIState(repo)
                    repoUtiStatesValue.add(uiState)
                    if (uiState.repo.state == Repo.STATE_INIT) {
                        clone(uiState)
                    }
                }
                repoUIStates.value = repoUtiStatesValue
            } catch (e: Exception) {
                e.printStackTrace()
                toast("Refresh repo list failed", e)
            }
        }
    }

    fun deleteRepo(uiState: RepoUIState) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { DaoManager.repoDao.delete(uiState.repo) }
                refreshRepoList()
            } catch (e: Exception) {
                e.printStackTrace()
                toast("Delete repo failed", e)
            }
        }
    }

}

/**
 * Represent repository list item view model
 */
class RepoUIState(val repo: Repo) {

    var progress = ""

    var statusText = ""

    var refresh: (() -> Unit)? = null

}