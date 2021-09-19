package sweet.wong.gmark.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig.Host
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.util.FS
import sweet.wong.gmark.core.noOpDelegate
import java.io.File

object Clone {

    const val TITLE_REMOTE_ENUMERATING_OBJECTS = "remote: Enumerating objects"
    const val TITLE_REMOTE_COUNTING_OBJECTS = "remote: Counting objects"
    const val TITLE_REMOTE_COMPRESSING_OBJECTS = "remote: Compressing objects"
    const val TITLE_RECEIVING_OBJECTS = "Receiving objects"
    const val TITLE_RESOLVING_DELTAS = "Resolving deltas"
    const val TITLE_UPDATING_REFERENCES = "Updating references"

    fun clone(
        url: String,
        localPath: String,
        username: String? = null,
        password: String? = null,
        ssh: String? = null,
        callback: CloneCallback
    ) {
        try {
            // Delete local repository
            File(localPath).deleteRecursively()

            val cloneCommand = Git.cloneRepository()
                .setURI(url)
                .setProgressMonitor(RepoCloneMonitor(callback))
                .setDirectory(File(localPath))
                .setCloneSubmodules(false)
                // for SSH clone
                .apply {
                    if (ssh != null) {
                        setTransportConfigCallback {
                            (it as? SshTransport)?.sshSessionFactory = SshSessionFactory(ssh)
                        }
                    }
                }
                // for HTTP clone
                .apply {
                    if (username != null && password != null)
                        setCredentialsProvider(
                            UsernamePasswordCredentialsProvider(
                                username,
                                password
                            )
                        )
                }

            cloneCommand.call()
        } catch (e: Exception) {
            callback.onFailed(e)
        }
    }

    interface CloneCallback {

        fun onSuccess()

        fun onProgress(title: String?, percent: Int)

        fun onFailed(e: Exception)

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

    class RepoCloneMonitor(private val callback: CloneCallback) : ProgressMonitor {

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

            callback.onProgress(mTitle, mLastProgress)

            if (mTitle == TITLE_UPDATING_REFERENCES && mLastProgress == 100) {
                callback.onSuccess()
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


}