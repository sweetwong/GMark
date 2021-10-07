package sweet.wong.gmark.git

import org.eclipse.jgit.api.TransportCommand
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import sweet.wong.gmark.data.Repo

fun TransportCommand<*, *>.setCredential(repo: Repo) = apply {
    // for SSH clone
    repo.ssh?.let { ssh ->
        setTransportConfigCallback { transport ->
            (transport as? SshTransport)?.sshSessionFactory = SshSessionFactory(ssh)
        }
    }
    // for HTTP clone
    repo.username?.let { username ->
        repo.password?.let { password ->
            setCredentialsProvider(
                UsernamePasswordCredentialsProvider(username, password)
            )
        }
    }
}