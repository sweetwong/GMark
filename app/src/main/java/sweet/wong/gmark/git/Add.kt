package sweet.wong.gmark.git

import sweet.wong.gmark.data.Repo

object Add {

    fun start(repo: Repo) {
        val git = repo.git

        git.add()
    }

}