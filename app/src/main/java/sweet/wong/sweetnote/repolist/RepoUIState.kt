package sweet.wong.sweetnote.repolist

import sweet.wong.sweetnote.data.Repo

/**
 * Represent repository list item view model
 */
class RepoUIState(val repo: Repo) {

    var position = -1

    var progress = ""

    var statusText = ""

}