package sweet.wong.gmark.git

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import sweet.wong.gmark.data.Repo

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/7
 */
object Pull {

    fun start(repo: Repo) = callbackFlow {
        try {
            val git = repo.git
            val config = git.repository.config
            val remotes: Set<String> = config.getSubsections("remote")
            val remote = remotes.first()
            git.pull()
                .setProgressMonitor(BasicProgressMonitor {
                    trySend(
                        GitResult.Progress(
                            "${it.message} ${it.leftHint} ${it.rightHint}",
                            it.progress
                        )
                    )
                })
                .setRemote(remote)
                .setCredential(repo)
                .call()
        } catch (e: Exception) {
            e.printStackTrace()
            trySend(GitResult.Failure(e))
        }
        awaitClose()
    }

}