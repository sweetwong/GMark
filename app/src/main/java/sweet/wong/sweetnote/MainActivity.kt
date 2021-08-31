package sweet.wong.sweetnote

import android.graphics.Color
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.PathUtils
import com.google.android.material.navigation.NavigationView
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var tvMarkdown: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initData()
    }

    private fun initView() {
        findViews()
        initToolbar()
        initDrawer()
    }

    private fun findViews() {
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    private fun initDrawer() {
        val actionBarDrawerToggle = ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.app_name,
                R.string.app_name
        )
        actionBarDrawerToggle.drawerArrowDrawable.color = Color.WHITE
        actionBarDrawerToggle.syncState()
    }

    private fun initData() {
        thread(true) {
            val cloneCommand = Git.cloneRepository()
                    .setURI("https://github.com/sweetwong/Android-Interview-QA.git")
                    .setProgressMonitor(RepoCloneMonitor())
                    .setDirectory(File(PathUtils.getExternalAppFilesPath() + "/QA"))
                    .setCloneSubmodules(false)
                    .setCredentialsProvider(UsernamePasswordCredentialsProvider("sweetwong", "123Woaini!!"))

            cloneCommand.call()
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

