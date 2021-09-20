package sweet.wong.gmark.git

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.util.FS
import sweet.wong.gmark.core.noOpDelegate

/**
 * https://stackoverflow.com/questions/53134212/invalid-privatekey-when-using-jsch
 *
 * ssh-keygen -t rsa -m PEM
 */
class SshSessionFactory(private val ssh: String) : JschConfigSessionFactory() {
    override fun configure(host: OpenSshConfig.Host?, session: Session) {
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

    override fun getJSch(hc: OpenSshConfig.Host?, fs: FS?): JSch {
        val jsch = super.getJSch(hc, fs)
        jsch.removeAllIdentity()
        jsch.addIdentity(ssh)
        return jsch
    }
}
