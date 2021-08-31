package sweet.wong.sweetnote

import android.util.Log
import com.blankj.utilcode.util.PathUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor
import java.io.File

class Clone {

    fun foo() {
        val cloneCommand = Git.cloneRepository()
            .setURI("https://github.com/maks/MGit.git")
            .setProgressMonitor(RepoCloneMonitor())
            .setDirectory(File(PathUtils.getExternalAppFilesPath() + "/mygit"))
            .setCloneSubmodules(false)

        cloneCommand.call()
    }

    inner class RepoCloneMonitor : ProgressMonitor {

        override fun start(totalTasks: Int) {
            log("start", totalTasks)
        }

        override fun beginTask(title: String?, totalWork: Int) {
            log("beginTask", title, totalWork)
        }

        override fun update(completed: Int) {
            log("update", completed)
        }

        override fun endTask() {
            log("endTask")
        }

        override fun isCancelled(): Boolean {
            return false
        }
    }

}

fun log(vararg any: Any?) {
    Log.d("调试", any.joinToString(", "))
}