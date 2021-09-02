package sweet.wong.sweetnote.repolist

import androidx.lifecycle.ViewModel
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.data.RepoModel

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/2
 */
class RepoListViewModel : ViewModel() {

    val repoUIStates = NonNullLiveData<MutableList<RepoUIState>>(mutableListOf())

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
                refreshRepoList()
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

}