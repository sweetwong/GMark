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
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/2
 */
class RepoListViewModel : ViewModel() {

    val repoUIStates = NonNullLiveData<MutableList<RepoUIState>>(mutableListOf())

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
        val ssh = repo.ssh

        if (ssh != null) {
            Clone.cloneWithSsh(
                repo.url,
                repo.localPath,
                ssh,
                object : ProgressMonitor {

                    override fun start(totalTasks: Int) {
                        log("start", totalTasks)
                    }

                    override fun beginTask(title: String?, totalWork: Int) {
                        log("beginTask", title, totalWork)
                    }

                    override fun update(completed: Int) {
                        log("update", completed)
                    }

                    override fun endTask() {
                        log("endTask")
                    }

                    override fun isCancelled(): Boolean {
                        return false
                    }
                })
        }
    }

    companion object {

        const val SP_LOCAL_REPO_PATH = "sp_local_repo_path"

        private const val SSH_PRIVATE_KEY_PATH = "/storage/emulated/0/id_rsa"

        private const val REMOTE_URL = "git@github.com:sweetwong/Android-Interview-QA.git"

    }

}