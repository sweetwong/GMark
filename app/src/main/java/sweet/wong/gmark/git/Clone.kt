package sweet.wong.gmark.git

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import sweet.wong.gmark.data.Repo
import java.io.File

object Clone {

    fun start(repo: Repo): Flow<GitResult> = callbackFlow {
        try {
            // Delete old files
            val rootFile = File(repo.localPath)
            if (rootFile.isDirectory) {
                rootFile.deleteRecursively()
            }

            val cloneCommand = Git.cloneRepository()
                .setURI(repo.url)
                .setProgressMonitor(RepoProgressMonitor(this))
                .setDirectory(rootFile)
                .setCloneSubmodules(false)
                // for SSH clone
                .apply {
                    repo.ssh?.let { ssh ->
                        setTransportConfigCallback { transport ->
                            (transport as? SshTransport)?.sshSessionFactory = SshSessionFactory(ssh)
                        }
                    }
                }
                // for HTTP clone
                .apply {
                    if (repo.username != null && repo.password != null)
                        setCredentialsProvider(
                            UsernamePasswordCredentialsProvider(repo.username, repo.password)
                        )
                }

            cloneCommand.call()
        } catch (e: Exception) {
            send(GitResult.Failure(e))
        }
        awaitClose()
    }

}