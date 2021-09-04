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
import sweet.wong.gmark.core.toast
import java.io.File
import kotlin.concurrent.thread

object Clone {

    fun clone(
        url: String,
        localPath: String,
        username: String? = null,
        password: String? = null,
        ssh: String? = null,
        monitor: ProgressMonitor
    ) {
        // https://stackoverflow.com/questions/53134212/invalid-privatekey-when-using-jsch
        // ssh-keygen -t rsa -m PEM
        val sshSessionFactory = object : JschConfigSessionFactory() {
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
                ssh?.let {
                    jsch.removeAllIdentity()
                    jsch.addIdentity(it)
                }
                return jsch
            }
        }

        thread(true) {
            try {
                // Delete local repository
                File(localPath).deleteRecursively()

                val cloneCommand = Git.cloneRepository()
                    .setURI(url)
                    .setProgressMonitor(monitor)
                    .setDirectory(File(localPath))
                    .setCloneSubmodules(false)
                    // for SSH clone
                    .apply {
                        if (ssh != null) {
                            setTransportConfigCallback {
                                (it as? SshTransport)?.sshSessionFactory = sshSessionFactory
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
                toast("Clone failed", e)
            }
        }
    }

}