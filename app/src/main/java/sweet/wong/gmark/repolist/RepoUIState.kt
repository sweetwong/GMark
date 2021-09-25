package sweet.wong.gmark.repolist

import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.utils.UIState

/**
 * Represent repository list item view model
 */
class RepoUIState(val repo: Repo) : UIState() {

    var progress = ""

    var statusText = ""

}