package sweet.wong.gmark.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig.Host
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.util.FS
import sweet.wong.gmark.core.noOpDelegate
import sweet.wong.gmark.data.Repo
import java.io.File

object Clone {

    fun clone(repo: Repo): Flow<Result> = callbackFlow {
        try {
            // Delete old files
            val rootFile = File(repo.localPath)
            if (rootFile.isDirectory) {
                rootFile.deleteRecursively()
            }

            val cloneCommand = Git.cloneRepository()
                .setURI(repo.url)
                .setProgressMonitor(RepoCloneMonitor(this))
                .setDirectory(rootFile)
                .setCloneSubmodules(false)
                // for SSH clone
                .apply {
                    if (repo.ssh != null) {
                        setTransportConfigCallback {
                            (it as? SshTransport)?.sshSessionFactory = SshSessionFactory(repo.ssh!!)
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
            send(Result.Failure(e))
        }
        awaitClose()
    }

    sealed class Result {

        object Success : Result()

        class Progress(val title: String?, val percent: Int) : Result()

        class Failure(val e: Exception) : Result()

    }

    /**
     * https://stackoverflow.com/questions/53134212/invalid-privatekey-when-using-jsch
     * ssh-keygen -t rsa -m PEM
     */
    class SshSessionFactory(private val ssh: String) : JschConfigSessionFactory() {
        override fun configure(host: Host?, session: Session) {
            session.userInfo = object : UserInfo by noOpDelegate() {

                override fun getPassphrase(): String {
                    return "passphrase"
                }

                override fun promptPassphrase(message: String): Boolean {
                    return true
                }

                override fun promptYesNo(message: String): Boolean {
                    return true
                }
            }
        }

        override fun getJSch(hc: Host?, fs: FS?): JSch {
            val jsch = super.getJSch(hc, fs)
            jsch.removeAllIdentity()
            jsch.addIdentity(ssh)
            return jsch
        }
    }

    class RepoCloneMonitor(private val scope: ProducerScope<Result>) : ProgressMonitor {

        private var mTotalWork = 0
        private var mWorkDone = 0
        private var mLastProgress = 0
        private var mTitle: String? = null

        private fun publishProgressInner() {
            if (mTotalWork != 0) {
                val p = 100 * mWorkDone / mTotalWork
                if (p - mLastProgress < 1) {
                    return
                }
                mLastProgress = p
            }

            scope.trySend(Result.Progress(mTitle, mLastProgress))
            if (mTitle == TITLE_UPDATING_REFERENCES && mLastProgress == 100) {
                scope.trySend(Result.Success)
            }
        }

        override fun start(totalTasks: Int) {
            publishProgressInner()
        }

        override fun beginTask(title: String?, totalWork: Int) {
            mTotalWork = totalWork
            mWorkDone = 0
            mLastProgress = 0
            mTitle = title
            publishProgressInner()
        }

        override fun update(i: Int) {
            mWorkDone += i
            publishProgressInner()
        }

        override fun endTask() {}

        override fun isCancelled(): Boolean {
            return false
        }
    }

    const val TITLE_REMOTE_ENUMERATING_OBJECTS = "remote: Enumerating objects"
    const val TITLE_REMOTE_COUNTING_OBJECTS = "remote: Counting objects"
    const val TITLE_REMOTE_COMPRESSING_OBJECTS = "remote: Compressing objects"
    const val TITLE_RECEIVING_OBJECTS = "Receiving objects"
    const val TITLE_RESOLVING_DELTAS = "Resolving deltas"
    const val TITLE_UPDATING_REFERENCES = "Updating references"

}