package sweet.wong.gmark.repo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.tabs.TabLayoutMediator
import sweet.wong.gmark.R
import sweet.wong.gmark.core.noOpDelegate
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.databinding.ActivityRepoBinding
import sweet.wong.gmark.settings.SettingsActivity
import sweet.wong.gmark.utils.EventObserver

class RepoActivity : AppCompatActivity() {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var binding: ActivityRepoBinding
    private lateinit var drawerDelegate: DrawerDelegate

    private lateinit var viewPagerAdapter: RepoPagerAdapter
    private val fragments: MutableList<RepoFragment> = mutableListOf()

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

        // Init toolbar
        setSupportActionBar(binding.toolbar)
        initDrawer(savedInstanceState)
        initFragments()
        initViewPager()
        initTabs()
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

    private fun initFragments() {
        repeat(viewModel.tabLimit) {
            fragments.add(RepoFragment(it))
        }
    }

    private fun initViewPager() = with(binding.viewPager) {
        viewPagerAdapter = RepoPagerAdapter(this@RepoActivity, fragments)
        adapter = viewPagerAdapter
        registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                viewModel.currentPagePosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
    }

    private fun initTabs() = with(binding) {
        TabLayoutMediator(tabLayout, viewPager, true, true) { tab, position ->
            val pages = viewModel.pages
            if (position < pages.size) {
                tab.text = pages[position].title
            }
        }.attach()
    }

    /**
     * View model observers
     */
    private fun initObservers() {
        viewModel.selectFileEvent.observe(this, EventObserver {
            binding.toolbar.title = it.file.name
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

