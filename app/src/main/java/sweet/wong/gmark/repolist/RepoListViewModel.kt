package sweet.wong.gmark.repolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.R
import sweet.wong.gmark.core.getString
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.ext.IO_CATCH
import sweet.wong.gmark.ext.MAIN_CATCH
import sweet.wong.gmark.git.Clone
import sweet.wong.gmark.git.Progress
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
        uiState.state = Repo.STATE_SYNCING
        uiState.updateUI()

        Clone.start(uiState.repo).subscribe(object : Observer<Progress> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(p: Progress) {
                uiState.updateUI {
                    state = Repo.STATE_SYNCING
                    summary = p.combinedMessage
                    progress = p.progress
                }
            }

            override fun onError(e: Throwable) {
                uiState.updateUI {
                    state = Repo.STATE_FAILED
                    summary = getString(R.string.summary_clone_failed)
                    progress = 0
                }
                updateRepo(uiState.repo)
                toast("Clone failed", e)
            }

            override fun onComplete() {
                uiState.updateUI {
                    state = Repo.STATE_SUCCESS
                    summary = TimeUtils.getRelativeString()
                    progress = 0
                }
                updateRepo(uiState.repo)
            }
        })
    }

    fun pull(uiState: RepoUIState) = viewModelScope.launch {
        Pull.start(uiState.repo).subscribe(object : Observer<Progress> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(p: Progress) {
                uiState.updateUI {
                    summary = p.combinedMessage
                    progress = p.progress
                }
            }

            override fun onError(e: Throwable) {
                uiState.updateUI {
                    summary = getString(R.string.summary_pull_failed)
                    progress = 0
                }
                updateRepo(uiState.repo)
                toast("Pull failed", e)
            }

            override fun onComplete() {
                uiState.updateUI {
                    summary = TimeUtils.getRelativeString()
                    progress = 0
                }
                updateRepo(uiState.repo)
            }
        })
    }

    private fun updateRepo(repo: Repo) {
        viewModelScope.launch(Dispatchers.IO_CATCH) {
            DaoManager.repoDao.update(repo)
        }
    }

    // FIXME: 2021/9/2 这里刷新可能会导致丢失UI状态
    fun refreshRepoList() = viewModelScope.launch(Dispatchers.MAIN_CATCH) {
        val repos = withContext(Dispatchers.IO) { DaoManager.repoDao.getAll() }
        val repoUIStates = mutableListOf<RepoUIState>()
        repos.forEach { repo ->
            val uiState = RepoUIState(repo)
            repoUIStates.add(uiState)
            when (uiState.state) {
                Repo.STATE_INIT -> {
                    uiState.summary = getString(R.string.summary_prepare_clone)
                    clone(uiState)
                }
                Repo.STATE_SUCCESS -> {
                    uiState.summary = TimeUtils.getRelativeString(uiState.repo.time)
                }
                Repo.STATE_FAILED -> {
                    uiState.summary = getString(R.string.summary_clone_failed)
                }
            }
        }
        this@RepoListViewModel.repoUIStates.value = repoUIStates
    }


    fun deleteRepo(uiState: RepoUIState) = viewModelScope.launch(Dispatchers.MAIN_CATCH) {
        withContext(Dispatchers.IO) {
            // Delete sql
            DaoManager.repoDao.delete(uiState.repo)

            // Delete files
            File(uiState.repo.root).takeIf { it.isDirectory }?.deleteRecursively()
        }
        refreshRepoList()
    }

}