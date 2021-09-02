package sweet.wong.sweetnote.repolist

import androidx.lifecycle.ViewModel
import org.eclipse.jgit.lib.ProgressMonitor
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.core.log
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.data.RepoModel
import sweet.wong.sweetnote.git.Clone
import sweet.wong.sweetnote.utils.Utils

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

    private fun startClone() {
        val localRepoPath = Utils.getRepoPath(REMOTE_URL)
        log("repoPath", localRepoPath)

        Clone.cloneWithSsh(
            REMOTE_URL,
            localRepoPath,
            SSH_PRIVATE_KEY_PATH,
            object : ProgressMonitor {

                private var taskTitle: String? = null
                private var taskTotalWork: Int? = null

                override fun start(totalTasks: Int) {
                    log("start", totalTasks)
                }

                override fun beginTask(title: String?, totalWork: Int) {
                    log("beginTask", title, totalWork)
                    taskTitle = title
                    taskTotalWork = totalWork
                }

                override fun update(completed: Int) {
                    log("update", completed)
                }

                override fun endTask() {
                    log("endTask")
//                    if (taskTitle == "Updating references" && taskTotalWork == 2) {
//                        SPUtils.putString(SP_LOCAL_REPO_PATH, localRepoPath)
//                        startActivity(Intent(this@RepositoryActivity, PreviewActivity::class.java))
//                    }
                }

                override fun isCancelled(): Boolean {
                    return false
                }
            })
    }


    //        PermissionUtils.onGranted(Permission.Group.STORAGE) {
//            log("")
//            val localRepoPath = SPUtils.getString(SP_LOCAL_REPO_PATH)
//            if (localRepoPath == null) {
//                startClone()
//            } else {
//                startActivity(Intent(this@RepoListActivity, RepoDetailActivity::class.java))
//            }
//
//        }

    companion object {

        const val SP_LOCAL_REPO_PATH = "sp_local_repo_path"

        private const val SSH_PRIVATE_KEY_PATH = "/storage/emulated/0/id_rsa"

        private const val REMOTE_URL = "git@github.com:sweetwong/Android-Interview-QA.git"

    }

}