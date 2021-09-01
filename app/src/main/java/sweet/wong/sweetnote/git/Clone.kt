package sweet.wong.sweetnote.git

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
import sweet.wong.sweetnote.core.noOpDelegate
import java.io.File
import kotlin.concurrent.thread

object Clone {

    fun cloneWithHttp(
        url: String,
        localRepoPath: String,
        username: String,
        password: String,
        monitor: ProgressMonitor
    ) {
        clone(url, localRepoPath, null, username, password, monitor)
    }

    fun cloneWithSsh(
        url: String,
        localRepoPath: String,
        sshPrivateKeyPath: String,
        monitor: ProgressMonitor
    ) {
        clone(url, localRepoPath, sshPrivateKeyPath, null, null, monitor)
    }

    private fun clone(
        url: String,
        localRepoPath: String,
        sshPrivateKeyPath: String? = null,
        username: String? = null,
        password: String? = null,
        monitor: ProgressMonitor
    ) {
        // https://stackoverflow.com/questions/53134212/invalid-privatekey-when-using-jsch
        // 用 ssh-keygen -t rsa -m PEM 生成密钥
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
                sshPrivateKeyPath?.let {
                    jsch.removeAllIdentity()
                    jsch.addIdentity(it)
                }
                return jsch
            }
        }

        thread(true) {
            File(localRepoPath).deleteRecursively()

            val cloneCommand = Git.cloneRepository()
                .setURI(url)
                .setProgressMonitor(monitor)
                .setDirectory(File(localRepoPath))
                .setCloneSubmodules(false)
                .setTransportConfigCallback {
                    (it as SshTransport).sshSessionFactory = sshSessionFactory
                }
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
        }
    }

}