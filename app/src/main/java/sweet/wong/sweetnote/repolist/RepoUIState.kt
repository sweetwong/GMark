package sweet.wong.sweetnote.repolist

import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.data.Repo

/**
 * Represent repository list item view model
 *
 * @author sweetwang 2021/9/2
 */
class RepoUIState(data: Repo) {

    val repo = NonNullLiveData(data)

    val progress = NonNullLiveData(0)

    val tipText = NonNullLiveData("")

}