package sweet.wong.gmark.repo.drawer.git

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import sweet.wong.gmark.R
import sweet.wong.gmark.core.getString
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.ext.IO_CATCH
import sweet.wong.gmark.git.SshSessionFactory
import sweet.wong.gmark.sp.SPUtils
import java.io.File

class GitViewModel : ViewModel() {

    lateinit var repo: Repo

    val diffUIStates = MutableLiveData<List<DiffUIState>>()

    val commitMessage = MutableLiveData<String>()

    private val git: Git
        get() = Git.open(File(repo.localPath))

    fun refreshDiffList() = viewModelScope.launch(Dispatchers.IO_CATCH) {
        val uiStates = mutableListOf<DiffUIState>()
        git.diff().call().forEach {
            uiStates.add(DiffUIState(it))
        }
        diffUIStates.postValue(uiStates)
    }

    fun commit() {
        val commitMessage = this.commitMessage.value
        if (commitMessage.isNullOrBlank()) {
            toast("Commit message is empty")
            return
        }

        val username = SPUtils.settings.getString(getString(R.string.pref_user_name), null)
        if (username.isNullOrEmpty()) {
            toast("Please set your username")
            return
        }

        val email = SPUtils.settings.getString(getString(R.string.pref_user_email), null)
        if (email.isNullOrEmpty()) {
            toast("Please set your email")
            return
        }

        viewModelScope.launch(Dispatchers.IO_CATCH) {
            val git = this@GitViewModel.git
            // Add all
            git.add()
                .addFilepattern(".")
                .call()

            // Commit
            git.commit()
                .setAuthor(username, email)
                .setAll(true)
                .setMessage(commitMessage)
                .call()

            toast("Commit success")
        }
    }

    fun push() {
        viewModelScope.launch(Dispatchers.IO_CATCH) {
            val git = this@GitViewModel.git

            git.push()
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
                .call()

            toast("Push success")
        }
    }

}