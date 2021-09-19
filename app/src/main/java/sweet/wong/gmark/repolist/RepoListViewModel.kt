package sweet.wong.gmark.repolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.TimeUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.git.Clone
import sweet.wong.gmark.utils.Event
import sweet.wong.gmark.utils.NonNullLiveData
import java.io.File

/**
 * Git repository list view model
 */
class RepoListViewModel : ViewModel() {

    val repoUIStates = NonNullLiveData<MutableList<RepoUIState>>(mutableListOf())

    val repoSelectEvent = MutableLiveData<Event<Repo>>()

    private fun clone(uiState: RepoUIState) = viewModelScope.launch(Dispatchers.IO) {
        Clone.clone(uiState.repo).collect { result ->
            when (result) {
                is Clone.Result.Success -> {
                    uiState.repo.state = Repo.STATE_SUCCESS
                    uiState.statusText = TimeUtils.date2String(TimeUtils.getNowDate())
                    DaoManager.repoDao.update(uiState.repo)
                    withContext(Dispatchers.Main) {
                        uiState.refresh?.invoke()
                    }
                }
                is Clone.Result.Failure -> {
                    result.e.printStackTrace()
                    toast("Clone failed", result.e)
                    uiState.repo.state = Repo.STATE_FAILED
                    DaoManager.repoDao.update(uiState.repo)
                }
                is Clone.Result.Progress -> {
                    log(
                        "Git clone onProgress", "title", result.title, "percent", result.percent
                    )
                    uiState.repo.state = Repo.STATE_SYNCING
                    uiState.statusText = "${result.title} ... (${result.percent}%)"
                    uiState.progress = result.percent.toString()
                    withContext(Dispatchers.Main) {
                        uiState.refresh?.invoke()
                    }
                }
            }
        }
    }

    // FIXME: 2021/9/2 这里刷新可能会导致丢失UI状态
    fun refreshRepoList() = viewModelScope.launch {
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


    fun deleteRepo(uiState: RepoUIState) = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                // Delete sql
                DaoManager.repoDao.delete(uiState.repo)

                // Delete files
                File(uiState.repo.localPath).takeIf { it.isDirectory }?.deleteRecursively()
            }
            refreshRepoList()
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Delete repo failed", e)
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