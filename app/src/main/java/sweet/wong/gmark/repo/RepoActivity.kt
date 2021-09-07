package sweet.wong.gmark.repo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ScreenUtils
import sweet.wong.gmark.R
import sweet.wong.gmark.core.noOpDelegate
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.databinding.ActivityRepoBinding
import sweet.wong.gmark.repo.project.ProjectFragment
import sweet.wong.gmark.repo.markdown.MarkdownDelegate
import sweet.wong.gmark.utils.EventObserver
import sweet.wong.gmark.utils.SnappingLinearLayoutManager

class RepoActivity : AppCompatActivity() {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var binding: ActivityRepoBinding
    private lateinit var markdown: MarkdownDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Parse argument
        val repo = intent.getParcelableExtra<Repo>(EXTRA_REPO)
        if (repo == null) {
            finish()
            return
        }

        // Binding View
        binding = ActivityRepoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add<ProjectFragment>(R.id.fragment_container_view)
            }
        }

        // Init toolbar
        setSupportActionBar(binding.toolbar)

        // Init drawer toggle
        val drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        drawerToggle.syncState()

        binding.drawerLayout.addDrawerListener(object :
            DrawerLayout.DrawerListener by noOpDelegate() {

            override fun onDrawerClosed(drawerView: View) {
                viewModel.updateDrawer()
            }
        })
        // Navigation bar show 90% width
        binding.navigationView.layoutParams = binding.navigationView.layoutParams.apply {
            width = (ScreenUtils.getScreenWidth() * 0.9).toInt()
        }

        binding.btnProject.setOnClickListener(::onClickDrawerButton)
        binding.btnGit.setOnClickListener(::onClickDrawerButton)
        binding.btnHistory.setOnClickListener(::onClickDrawerButton)
        binding.btnHistory.setOnClickListener(::onClickDrawerButton)

        // Init Markdown
        markdown = MarkdownDelegate(viewModel)

        // Record current page scroll Y
        // Used for restore page
        binding.markList.layoutManager = SnappingLinearLayoutManager(this)
        binding.markList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.scrollY += dy
            }
        })

        binding.drawerToolbar.setNavigationOnClickListener {
            binding.drawerLayout.closeDrawer(binding.navigationView)
        }

        // View model observers
        viewModel.rawText.observe(this) {
            // FIXME: 2021/9/7 这里这个时机是错误的
            val fileName = viewModel.showingFile?.name ?: ""
            markdown.setMarkdown(fileName, binding.markList, it)
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

        // Start load data
        viewModel.init(repo)
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

    private fun onClickDrawerButton(button: View) {
        if (button !is ImageButton) {
            return
        }

        when (button) {
            binding.btnProject -> {

            }
            binding.btnGit -> {

            }
            binding.btnHistory -> {

            }
            binding.btnOutline -> {

            }
        }
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

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
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

