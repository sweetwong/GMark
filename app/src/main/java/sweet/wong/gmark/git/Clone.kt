package sweet.wong.gmark.git

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.eclipse.jgit.api.Git
import sweet.wong.gmark.data.Repo
import java.io.File

object Clone {

    fun start(repo: Repo): Flow<GitResult> = callbackFlow {
        try {
            // Delete old files
            val rootFile = File(repo.root)
            if (rootFile.isDirectory) {
                rootFile.deleteRecursively()
            }

            Git.cloneRepository()
                .setURI(repo.url)
                .setProgressMonitor(RepoProgressMonitor(this))
                .setDirectory(rootFile)
                .setCredential(repo)
                .call()
        } catch (e: Exception) {
            e.printStackTrace()
            send(GitResult.Failure(e))
            close()
        }
        awaitClose()
    }

}