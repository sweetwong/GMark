package sweet.wong.gmark.repo

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import sweet.wong.gmark.R
import sweet.wong.gmark.core.noOpDelegate
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.databinding.ActivityRepoBinding
import sweet.wong.gmark.repo.markdown.MarkdownDelegate
import sweet.wong.gmark.settings.SettingsActivity
import sweet.wong.gmark.utils.EventObserver
import sweet.wong.gmark.utils.SnappingLinearLayoutManager

class RepoActivity : AppCompatActivity() {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var binding: ActivityRepoBinding

    private lateinit var drawerDelegate: DrawerDelegate
    private lateinit var markdownDelegate: MarkdownDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Parse argument
        val repo = intent.getParcelableExtra<Repo>(EXTRA_REPO)
        if (repo == null) {
            finish()
            return
        }

        // Binding View
        binding = ActivityRepoBinding.inflate(layoutInflater).apply {
            setContentView(root)
            viewModel = this@RepoActivity.viewModel
            lifecycleOwner = this@RepoActivity
        }

        // Init markdown
        markdownDelegate = MarkdownDelegate(viewModel)

        // Init toolbar
        setSupportActionBar(binding.toolbar)
        initDrawer(savedInstanceState)
        initMarkList()
        initObservers()

        // Start load data
        viewModel.init(repo)
    }

    private fun initDrawer(savedInstanceState: Bundle?) {
        binding.drawerLayout.addDrawerListener(object : DrawerListener by noOpDelegate() {

            override fun onDrawerClosed(drawerView: View) {
                viewModel.updateDrawer()
            }
        })

        // Navigation bar show 90% width
        binding.navigationView.layoutParams = binding.navigationView.layoutParams.apply {
            width = (ScreenUtils.getScreenWidth() * 0.9).toInt()
        }

        // Init drawer toggle
        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        drawerToggle.syncState()

        drawerDelegate = DrawerDelegate(this, viewModel, binding.includeLayoutDrawer)
        drawerDelegate.onCreate(savedInstanceState)
    }


    private fun initMarkList() {
        binding.markList.layoutManager = SnappingLinearLayoutManager(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.markList.isForceDarkAllowed = false
        }
        // Record current page scroll Y
        // Used for restore page
        binding.markList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.scrollY += dy
            }
        })
    }

    /**
     * View model observers
     */
    private fun initObservers() {
        viewModel.fileRaw.observe(this) {
            markdownDelegate.setMarkdown(it.file.name, binding.markList, it.raw)
        }

        viewModel.selectFileEvent.observe(this, EventObserver {
            binding.toolbar.title = it.file.name
            scrollY(it.scrollY)
            // If drawer is visible, we close drawer
            // Note that close drawer will trigger update drawer
            if (binding.drawerLayout.isDrawerVisible(binding.navigationView)) {
                binding.drawerLayout.closeDrawer(binding.navigationView)
            }
            // If drawer is not visible, we just need update drawer
            else {
                viewModel.updateDrawer()
            }
        })

        viewModel.drawerShowEvent.observe(this, EventObserver { open ->
            if (open) binding.drawerLayout.openDrawer(binding.navigationView)
            else binding.drawerLayout.closeDrawer(binding.navigationView)
        })
    }

    /**
     * Restore scroll history
     */
    private fun scrollY(scrollY: Int) {
        // Scroll
        binding.markList.scrollBy(0, scrollY)

        // If it's new page, run animation
        if (scrollY == 0) {
            val animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
            binding.markList.startAnimation(animation)
        }
    }

    /**
     * Intercept back pressed
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.drawerLayout.isDrawerVisible(binding.navigationView)) {
                binding.drawerLayout.closeDrawer(binding.navigationView)
                return true
            }

            // Handle main text back stack
            if (viewModel.restorePage()) {
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_repo_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sync -> {
                toast("Sync")
                return true
            }
            R.id.menu_settings -> {
                SettingsActivity.start(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val EXTRA_REPO = "extra_repo"

        fun start(context: Context, repo: Repo) {
            context.startActivity(
                Intent(context, RepoActivity::class.java).putExtra(EXTRA_REPO, repo)
            )
        }

    }

}

