package sweet.wong.sweetnote.repolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.eclipse.jgit.lib.ProgressMonitor
import sweet.wong.sweetnote.core.Event
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.core.log
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.data.RepoModel
import sweet.wong.sweetnote.git.Clone

/**
 * Git repository list view model
 */
class RepoListViewModel : ViewModel() {

    val repoUIStates = NonNullLiveData<MutableList<RepoUIState>>(mutableListOf())

    val repoUpdateEvent = MutableLiveData<Event<Int>>()
    val repoSelectEvent = MutableLiveData<Event<Repo>>()

    fun refreshRepoList() {
        // FIXME: 2021/9/2 这里刷新可能会导致丢失UI状态
        RepoModel.getAll()
            .doOnNext {
                val repoUtiStatesValue = mutableListOf<RepoUIState>()
                it.forEach { repo ->
                    repoUtiStatesValue.add(RepoUIState(repo))
                }
                repoUIStates.value = repoUtiStatesValue
            }
            .doOnError {
                toast("Refresh repo list failed", it)
            }
            .subscribe()
    }

    fun addNewRepo(repo: Repo) {
        RepoModel.insertAll(repo)
            .doOnNext {
                val repoUIState = RepoUIState(repo)
                repoUIStates.value = repoUIStates.value.apply { add(repoUIState) }
                startClone(repoUIState)
            }
            .doOnError {
                toast("Add new repo failed", it)
            }
            .subscribe()
    }

    fun deleteRepo(repo: Repo) {
        RepoModel.delete(repo)
            .doOnNext {
                refreshRepoList()
            }
            .doOnError {
                toast("Delete repo failed", it)
            }
            .subscribe()
    }

    private fun startClone(repoUIState: RepoUIState) {
        val repo = repoUIState.repo

        // TODO: 2021/9/3 添加进度条
        Clone.clone(
            repo.url,
            repo.localPath,
            repo.username,
            repo.password,
            repo.ssh,
            RepoCloneMonitor(this, repoUIState))
    }

}