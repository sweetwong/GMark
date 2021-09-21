package sweet.wong.gmark.git

import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.junit.Test

class PushTest {

    @Test
    fun push() {
        git.push()
            .setRemote("https://github.com/sweetwong/GMark.git")
            .setCredentialsProvider(
                UsernamePasswordCredentialsProvider(
                    "sweetwong",
                    "123Woaini!!"
                )
            )
            .setProgressMonitor(object : ProgressMonitor {
                override fun start(totalTasks: Int) {
                    println("start, totalTasks: $totalTasks")
                }

                override fun beginTask(title: String?, totalWork: Int) {
                    println("beginTask, title: $title, totalWork: $totalWork")
                }

                override fun update(completed: Int) {
                    println("update, completed: $completed")
                }

                override fun endTask() {
                    println("endTask")
                }

                override fun isCancelled(): Boolean {
                    println("isCancelled")
                    return false
                }
            })
            .call()
    }

}