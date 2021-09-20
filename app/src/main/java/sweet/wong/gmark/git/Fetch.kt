package sweet.wong.gmark.git

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import sweet.wong.gmark.data.Repo

object Fetch {

    fun start(repo: Repo) = callbackFlow {
        try {
            val git = repo.git
            val config = git.repository.config
            val remotes: Set<String> = config.getSubsections("remote")
            val remote = remotes.first()
            git.fetch()
                .setProgressMonitor(RepoProgressMonitor(this@callbackFlow))
                .setRemote(remote)
                .call()
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(GitResult.Failure(e))
        }
        awaitClose()
    }

}