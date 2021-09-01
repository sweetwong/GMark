package sweet.wong.sweetnote.repolist

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.PathUtils
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.runtime.Permission
import org.eclipse.jgit.lib.ProgressMonitor
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.core.log
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.git.Clone
import sweet.wong.sweetnote.repodetail.RepoDetailActivity
import sweet.wong.sweetnote.utils.SPUtils

/**
 * 仓库选择页面
 *
 * @author sweetwang 2021/9/1
 */
class RepoListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        AndPermission.with(this)
            .runtime()
            .permission(Permission.Group.STORAGE)
            .onGranted {
                log("")
                val localRepoPath = SPUtils.getString(SP_LOCAL_REPO_PATH)
                if (localRepoPath == null)
                    startClone()
                else
                    startActivity(Intent(this@RepoListActivity, RepoDetailActivity::class.java))

            }
            .onDenied {
                toast("Permissions are denied")
            }
            .start()
    }

    private fun startClone() {
        val localRepoPath = getRepoPath(REMOTE_URL)
        log("repoPath", localRepoPath)

        Clone.cloneWithSsh(
            REMOTE_URL,
            localRepoPath,
            SSH_PRIVATE_KEY_PATH,
            object : ProgressMonitor {

                private var taskTitle: String? = null
                private var taskTotalWork: Int? = null

                override fun start(totalTasks: Int) {
                    log("start", totalTasks)
                }

                override fun beginTask(title: String?, totalWork: Int) {
                    log("beginTask", title, totalWork)
                    taskTitle = title
                    taskTotalWork = totalWork
                }

                override fun update(completed: Int) {
                    log("update", completed)
                }

                override fun endTask() {
                    log("endTask")
//                    if (taskTitle == "Updating references" && taskTotalWork == 2) {
//                        SPUtils.putString(SP_LOCAL_REPO_PATH, localRepoPath)
//                        startActivity(Intent(this@RepositoryActivity, PreviewActivity::class.java))
//                    }
                }

                override fun isCancelled(): Boolean {
                    return false
                }
            })
    }

    private fun getRepoPath(url: String): String {
        return PathUtils.getExternalAppFilesPath() + "/" + getTitleByGitUrl(url)
    }

    private fun getTitleByGitUrl(url: String): String {
        return StringBuilder().apply {
            url.replace(".git", "").reversed().forEach {
                if (it == '/') {
                    return reverse().toString()
                }
                append(it)
            }
        }.toString()
    }

    companion object {

        const val SP_LOCAL_REPO_PATH = "sp_local_repo_path"

        private const val SSH_PRIVATE_KEY_PATH = "/storage/emulated/0/id_rsa"

        private const val REMOTE_URL = "git@github.com:sweetwong/Android-Interview-QA.git"

    }

}