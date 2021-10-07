package sweet.wong.gmark.repolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.git.Clone
import sweet.wong.gmark.git.GitResult
import sweet.wong.gmark.git.Pull
import sweet.wong.gmark.utils.Event
import sweet.wong.gmark.utils.NonNullLiveData
import sweet.wong.gmark.utils.TimeUtils
import java.io.File

/**
 * Git repository list view model
 */
class RepoListViewModel : ViewModel() {

    val repoUIStates = NonNullLiveData<MutableList<RepoUIState>>(mutableListOf())

    val repoSelectEvent = MutableLiveData<Event<Repo>>()

    private fun clone(uiState: RepoUIState) = viewModelScope.launch {
        Clone.start(uiState.repo).flowOn(Dispatchers.IO).collect { result ->
            updateUI(uiState, result)
        }
    }

    fun pull(uiState: RepoUIState) = viewModelScope.launch {
        Pull.start(uiState.repo).flowOn(Dispatchers.IO).collect { result ->
            updateUI(uiState, result)
        }
    }

    private suspend fun updateUI(uiState: RepoUIState, result: GitResult) {
        when (result) {
            is GitResult.Success -> {
                withContext(Dispatchers.IO) {
                    uiState.repo.state = Repo.STATE_SUCCESS
                    uiState.statusText = TimeUtils.getRelativeString()
                    DaoManager.repoDao.update(uiState.repo)
                }
                uiState.updateUI()
            }
            is GitResult.Failure -> {
                toast("Clone failed", result.e)
                withContext(Dispatchers.IO) {
                    uiState.repo.state = Repo.STATE_FAILED
                    DaoManager.repoDao.update(uiState.repo)
                }
            }
            is GitResult.Progress -> {
                withContext(Dispatchers.IO) {
                    uiState.repo.state = Repo.STATE_SYNCING
                    uiState.statusText = "${result.title} ... (${result.percent}%)"
                    uiState.progress = result.percent
                }
                uiState.updateUI()
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
                    uiState.statusText = "Preparing clone ..."
                    uiState.updateUI()
                    clone(uiState)
                } else if (uiState.repo.state == Repo.STATE_SUCCESS) {
                    uiState.statusText = TimeUtils.getRelativeString(uiState.repo.time)
                    uiState.updateUI()
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
                File(uiState.repo.root).takeIf { it.isDirectory }?.deleteRecursively()
            }
            refreshRepoList()
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Delete repo failed", e)
        }
    }

}