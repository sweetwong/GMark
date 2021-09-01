package sweet.wong.sweetnote.repodetail

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import com.blankj.utilcode.util.PathUtils
import com.google.android.material.navigation.NavigationView
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.repolist.RepoListActivity
import sweet.wong.sweetnote.core.postDelayed
import sweet.wong.sweetnote.drawer.DrawerView
import sweet.wong.sweetnote.utils.SPUtils
import java.io.File

class RepoDetailActivity : AppCompatActivity() {

    private val viewModel: RepoDetailViewModel by viewModels()

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerView: DrawerView
    private lateinit var navigationView: NavigationView
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var markwon: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.path.value = SPUtils.getString(RepoListActivity.SP_LOCAL_REPO_PATH) + "/README.md"

        initView()
        initData()

        toolbar.isVisible = false
        viewModel.path.observe(this) {
            drawerLayout.closeDrawer(navigationView)
            postDelayed(180) {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                if (!drawerView.onBackPressed()) {
                    drawerLayout.closeDrawer(navigationView)
                }
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }
    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun initView() {
        findViews()
        initToolbar()
        initDrawer()
        initMarkdownPreview()
    }

    private fun initMarkdownPreview() {
        markwon = Markwon.builder(this)
            .usePlugins(listOf(GlideImagesPlugin.create(this), HtmlPlugin.create()))
            .build()
        markwon.setMarkdown(textView, File(viewModel.path.value.orEmpty()).readText())
    }

    private fun findViews() {
        toolbar = findViewById(R.id.toolbar)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        drawerView = findViewById(R.id.drawer_view)
        textView = findViewById(R.id.text_view)
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
//        CloneRepository.clone(object : ProgressMonitor by noOpDelegate() {})
    }

    companion object {

        private const val EXTRA_REPOSITORY_ROOT = "extra_repository_root"

        private val DEFAULT = PathUtils.getExternalAppFilesPath() + "/QA/QA.md"

    }

}

