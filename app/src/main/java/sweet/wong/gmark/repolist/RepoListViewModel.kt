package sweet.wong.gmark.repolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.blankj.utilcode.util.TimeUtils
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

    fun pull(uiState: RepoUIState) {

    }

    private fun clone(uiState: RepoUIState) {
        val repo = uiState.repo

        // TODO: 2021/9/3 添加进度条
        io {
            Clone.clone(
                repo.url,
                repo.localPath,
                repo.username,
                repo.password,
                repo.ssh,
                object : Clone.CloneCallback by noOpDelegate() {

                    override fun onProgress(title: String?, percent: Int) {
                        ui {
                            log("Git clone onProgress", "title", title, "percent", percent)
                            uiState.statusText = "$title ... ($percent%)"
                            uiState.progress = percent.toString()
                            uiState.refresh?.invoke()
                        }
                    }

                    override fun onSuccess() {
                        ui {
                            uiState.statusText = TimeUtils.date2String(TimeUtils.getNowDate())
                            uiState.refresh?.invoke()
                        }
                    }

                }
            )
        }
    }

    fun refreshRepoList() {
        // FIXME: 2021/9/2 这里刷新可能会导致丢失UI状态
        io {
            try {
                val repos = DaoManager.repoDao.getAll()
                ui {
                    val repoUtiStatesValue = mutableListOf<RepoUIState>()
                    repos.forEach { repo ->
                        repoUtiStatesValue.add(RepoUIState(repo))
                    }
                    repoUIStates.value = repoUtiStatesValue
                }
            } catch (e: Exception) {
                toast("Refresh repo list failed", e)
            }
        }
    }

    fun addNewRepo(repo: Repo) {
        io {
            try {
                DaoManager.repoDao.insertAll(repo)
                ui {
                    val repoUIState = RepoUIState(repo)
                    repoUIStates.value = repoUIStates.value.apply { add(repoUIState) }
                    clone(repoUIState)
                }
            } catch (e: Exception) {
                toast("Add new repo failed", e)
            }
        }
    }

    fun deleteRepo(uiState: RepoUIState) {
        io {
            try {
                DaoManager.repoDao.delete(uiState.repo)
                ui {
                    refreshRepoList()
                }
            } catch (e: Exception) {
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