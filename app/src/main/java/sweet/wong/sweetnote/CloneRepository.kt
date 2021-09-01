package sweet.wong.sweetnote

import com.blankj.utilcode.util.PathUtils
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig.Host
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.util.FS
import java.io.File
import kotlin.concurrent.thread


object CloneRepository {

    private const val SSH_PRIVATE_KEY = "/storage/emulated/0/id_rsa"
    private const val REMOTE_URL = "git@github.com:sweetwong/Android-Interview-QA.git"
    private val LOCAL_PATH = PathUtils.getExternalAppFilesPath() + "/QA"

    fun clone(monitor: ProgressMonitor) {
        // https://stackoverflow.com/questions/53134212/invalid-privatekey-when-using-jsch
        // 用 ssh-keygen -t rsa -m PEM 生成密钥
        val sshSessionFactory = object : JschConfigSessionFactory() {
            override fun configure(host: Host?, session: Session) {
                session.userInfo = object : UserInfo {

                    override fun getPassphrase(): String {
                        return "passphrase"
                    }

                    override fun getPassword(): String? {
                        return null
                    }

                    override fun promptPassword(message: String): Boolean {
                        return false
                    }

                    override fun promptPassphrase(message: String): Boolean {
                        return true
                    }

                    override fun promptYesNo(message: String): Boolean {
                        return true
                    }

                    override fun showMessage(message: String) {}
                }
            }


            override fun getJSch(hc: Host?, fs: FS?): JSch {
                val jsch = super.getJSch(hc, fs)
                jsch.removeAllIdentity()
                jsch.addIdentity(SSH_PRIVATE_KEY)
                return jsch
            }
        }



        thread(true) {
            File(LOCAL_PATH).deleteRecursively()

            val cloneCommand = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setProgressMonitor(monitor)
                .setDirectory(File(LOCAL_PATH))
                .setCloneSubmodules(false)
                .setTransportConfigCallback {
                    (it as SshTransport).sshSessionFactory = sshSessionFactory
                }

            cloneCommand.call()
        }

    }

}