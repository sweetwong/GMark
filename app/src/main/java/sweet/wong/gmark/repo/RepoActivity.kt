package sweet.wong.gmark.repo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import sweet.wong.gmark.R
import sweet.wong.gmark.core.EventObserver
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.postDelayed
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.databinding.ActivityRepoBinding
import sweet.wong.gmark.repo.markdown.MarkdownDelegate

class RepoActivity : AppCompatActivity() {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var binding: ActivityRepoBinding
    private lateinit var markdown: MarkdownDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding View
        binding = ActivityRepoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Parse argument
        val repo = intent.getParcelableExtra<Repo>(EXTRA_REPO)
        if (repo == null) {
            finish()
            return
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

        // Init Markdown
        markdown = MarkdownDelegate(viewModel)

        // Record current page scroll Y
        // Used for restore page
        binding.markList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.scrollY += dy
            }
        })

        // View model observers
        viewModel.raw.observe(this) {
            markdown.setMarkdown(binding.markList, it)
            binding.drawerLayout.closeDrawer(binding.navigationView)
        }

        viewModel.selectFileEvent.observe(this, EventObserver {
            binding.toolbar.title = it.name
            scrollY(0, true)
            binding.drawerView.refreshProject()
        })

        viewModel.drawerEvent.observe(this, EventObserver { open ->
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
            // Handle drawer view back
            if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
                if (!binding.drawerView.onBackPressed()) {
                    binding.drawerLayout.closeDrawer(binding.navigationView)
                }
                return true
            }

            // Handle main text back stack
            viewModel.popHistoryStack()?.let {
                binding.toolbar.title = it.file.name
                scrollY(it.scrollY, false)
                log("history file", it.file)
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_repo_list, menu)
        return true
    }

    /**
     * Restore scroll history
     */
    private fun scrollY(scrollY: Int, anim: Boolean) {
        binding.markList.scrollBy(0, scrollY)
        if (anim) {
            binding.markList.visibility = View.INVISIBLE
            postDelayed(60) {
                TransitionManager.beginDelayedTransition(binding.markList)
                binding.markList.visibility = View.VISIBLE
            }
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

