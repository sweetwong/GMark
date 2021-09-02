package sweet.wong.sweetnote.repolist

import androidx.lifecycle.ViewModel
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.data.Repo

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/2
 */
class RepoListViewModel : ViewModel() {

    val repos = NonNullLiveData<MutableList<RepoUIState>>(mutableListOf())

}