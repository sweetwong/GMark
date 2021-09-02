package sweet.wong.sweetnote.repolist

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import org.eclipse.jgit.lib.ProgressMonitor
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.core.log
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.git.Clone
import sweet.wong.sweetnote.utils.Utils
import java.util.*

/**
 * 仓库选择页面
 *
 * @author sweetwang 2021/9/1
 */
class RepoListActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var fab: FloatingActionButton
    private lateinit var repoList: RecyclerView

    private lateinit var repoListAdapter: RepoListAdapter

    private val viewModel: RepoListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_list)

        // 找到视图
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        fab = findViewById(R.id.fab)
        repoList = findViewById(R.id.repo_list)

        // 工具栏
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Repository List"

        // 悬浮按钮
        fab.setOnClickListener {
            RepoAuthDialogFragment().show(supportFragmentManager, null)
        }

        // 列表容器
        repoListAdapter = RepoListAdapter()
        repoList.adapter = repoListAdapter
        viewModel.repos.observe(this) {
            repoListAdapter.submitList(mutableListOf<NonNullLiveData<Repo>>().apply { addAll(it) })

            //        PermissionUtils.onGranted(Permission.Group.STORAGE) {
//            log("")
//            val localRepoPath = SPUtils.getString(SP_LOCAL_REPO_PATH)
//            if (localRepoPath == null) {
//                startClone()
//            } else {
//                startActivity(Intent(this@RepoListActivity, RepoDetailActivity::class.java))
//            }
//
//        }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_repo_list, menu)
        return true
    }

    private fun startClone() {
        val localRepoPath = Utils.getRepoPath(REMOTE_URL)
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

    companion object {

        const val SP_LOCAL_REPO_PATH = "sp_local_repo_path"

        private const val SSH_PRIVATE_KEY_PATH = "/storage/emulated/0/id_rsa"

        private const val REMOTE_URL = "git@github.com:sweetwong/Android-Interview-QA.git"

    }

}