package sweet.wong.gmark.repolist

import sweet.wong.gmark.data.Repo

/**
 * Represent repository list item view model
 */
class RepoUIState(val repo: Repo) {

    var position = -1

    var progress = ""

    var statusText = ""

}