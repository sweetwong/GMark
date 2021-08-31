package sweet.wong.sweetnote

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.PathUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var hello: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        hello = findViewById(R.id.hello)

        hello.setOnClickListener {
            thread(true) {
                val cloneCommand = Git.cloneRepository()
                        .setURI("https://github.com/maks/MGit.git")
                        .setProgressMonitor(RepoCloneMonitor())
                        .setDirectory(File(PathUtils.getExternalAppFilesPath() + "/mygit"))
                        .setCloneSubmodules(false)

                cloneCommand.call()
            }
        }

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