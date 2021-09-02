package sweet.wong.sweetnote.repolist

import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.data.RepoModel

/**
 * Represent repository list item view model
 *
 * @author sweetwang 2021/9/2
 */
class RepoUIState(data: Repo) {

    val repo = NonNullLiveData(data)

    val progress = NonNullLiveData(0)

    val tipText = NonNullLiveData("")

    fun updateRepo(new: Repo) {
        RepoModel.update(new)
            .doOnNext {
                repo.value = new
            }
            .doOnError {
                toast("updateRepo failed", it)
            }
            .subscribe()
    }

}