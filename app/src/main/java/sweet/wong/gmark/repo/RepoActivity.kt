package sweet.wong.gmark.repo

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.ObservableList
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.tabs.TabLayout
import sweet.wong.gmark.R
import sweet.wong.gmark.core.delay
import sweet.wong.gmark.core.noOpDelegate
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.databinding.ActivityRepoBinding
import sweet.wong.gmark.ext.start
import sweet.wong.gmark.repo.drawer.DrawerDelegate
import sweet.wong.gmark.repo.markdown.MarkdownFragment
import sweet.wong.gmark.repo.viewmodel.RepoViewModel
import sweet.wong.gmark.repolist.RepoListActivity
import sweet.wong.gmark.settings.SettingsActivity
import sweet.wong.gmark.sp.SPUtils.settings
import sweet.wong.gmark.utils.EventObserver
import sweet.wong.gmark.utils.OnListChangedCallback
import kotlin.math.min

class RepoActivity : AppCompatActivity() {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var binding: ActivityRepoBinding

    private lateinit var drawerDelegate: DrawerDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!viewModel.init()) {
            RepoListActivity.start(this)
            finish()
            return
        }

        // Binding View
        binding = ActivityRepoBinding.inflate(layoutInflater).apply {
            setContentView(root)
            viewModel = this@RepoActivity.viewModel
            lifecycleOwner = this@RepoActivity
        }

        initToolbar(viewModel.repo.name)
        initFragment(savedInstanceState)
        initDrawer(savedInstanceState)
        initTabLayout()
        initObservers()

        // load README.md
        if (savedInstanceState == null) {
            viewModel.loadREADME()
        }
    }

    override fun onStart() {
        super.onStart()
        val hideToolbar = settings.getBoolean(getString(R.string.pref_hide_toolbar), false)
        binding.toolbar.isVisible = !hideToolbar

        val hideTabLayout = settings.getBoolean(getString(R.string.pref_hide_tab_layout), false)
        binding.tabLayout.isVisible = !hideTabLayout
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        binding.tabLayout.apply {
            viewModel.pages.forEach {
                val tab = newTab().apply { text = it.file.name }
                addTab(tab)
                if (it == viewModel.showingPage.value) {
                    delay(10) {
                        selectTab(tab)
                    }
                }
            }
        }
        viewModel.updateDrawer()
    }

    private fun initToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add<MarkdownFragment>(R.id.markdown_fragment_container)
            }
        }
    }

    private fun initDrawer(savedInstanceState: Bundle?) {
        binding.drawerLayout.addDrawerListener(object : DrawerListener by noOpDelegate() {

            override fun onDrawerOpened(drawerView: View) {
                viewModel.onDrawerShow.value = true

            }

            override fun onDrawerClosed(drawerView: View) {
                viewModel.onDrawerShow.value = false
                viewModel.updateDrawer()
            }
        })

        // Navigation bar show 90% width
        binding.navigationView.layoutParams = binding.navigationView.layoutParams.apply {
            width = (min(
                ScreenUtils.getAppScreenWidth(),
                ScreenUtils.getAppScreenHeight()
            ) * 0.9).toInt()
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

    private fun initTabLayout() = with(binding.tabLayout) {
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener by noOpDelegate() {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.currentTabPosition = tab.position
                viewModel.selectFile(tab.position)
            }
        })

        viewModel.pages.addOnListChangedCallback(object :
            OnListChangedCallback<ObservableList<Page>>() {

            override fun onItemRangeChanged(
                sender: ObservableList<Page>,
                positionStart: Int,
                itemCount: Int
            ) {
                selectTab(getTabAt(positionStart))
            }

            override fun onItemRangeInserted(
                sender: ObservableList<Page>,
                positionStart: Int,
                itemCount: Int
            ) {
                val tab = newTab().apply { text = sender[positionStart].file.name }
                addTab(tab)
                delay(10) {
                    selectTab(tab)
                }
            }

            override fun onItemRangeRemoved(
                sender: ObservableList<Page>,
                positionStart: Int,
                itemCount: Int
            ) {
                if (sender.isEmpty()) {
                    finish()
                    return
                }

                // This will trigger onTabSelected
                binding.tabLayout.removeTabAt(positionStart)
            }
        })
    }

    /**
     * View model observers
     */
    private fun initObservers() {
        viewModel.showDrawer.observe(this, EventObserver { open ->
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

            if (viewModel.removeShowingPage()) {
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
            R.id.menu_change_repository -> {
                RepoListActivity.start(this)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun start(context: Context) = context.start<RepoActivity>()

    }

}

