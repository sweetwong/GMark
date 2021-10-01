package sweet.wong.gmark.repo

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.tabs.TabLayoutMediator
import sweet.wong.gmark.R
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.core.delay
import sweet.wong.gmark.core.noOpDelegate
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.ActivityRepoBinding
import sweet.wong.gmark.editor.EditorActivity
import sweet.wong.gmark.ext.notify
import sweet.wong.gmark.ext.start
import sweet.wong.gmark.repo.drawer.DrawerDelegate
import sweet.wong.gmark.repolist.RepoListActivity
import sweet.wong.gmark.settings.SettingsActivity
import sweet.wong.gmark.sp.SPUtils.settings
import sweet.wong.gmark.utils.EventObserver
import kotlin.math.min

class RepoActivity : BaseActivity<ActivityRepoBinding>() {

    private val viewModel: RepoViewModel by viewModels()

    private lateinit var drawerDelegate: DrawerDelegate

    private val editorLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // File has changed, now we force update
            viewModel.selectFile(viewModel.showingFile, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!viewModel.init()) {
            RepoListActivity.start(this)
            finish()
            return
        }

        // Binding View
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initToolbar(viewModel.repo.name)
        initViewPager()
        initDrawer(savedInstanceState)

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
            viewModel.pages.value.forEach {
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

    private lateinit var pageAdapter: RepoPageAdapter
    private fun initViewPager() {
        pageAdapter = RepoPageAdapter(this, viewModel)
        binding.viewPager.adapter = pageAdapter
        viewModel.pages.observe(this) { pages ->
            pageAdapter.submitList(pages.toMutableList()) {
                viewModel.pages.value.indexOf(viewModel.showingPage.value).takeIf { it != -1 }
                    ?.let { binding.viewPager.currentItem = it }
            }
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.showingPage.value = viewModel.pages.value[position]
            }
        })
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = pageAdapter.currentList[position].file.name
        }.attach()
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

            if (viewModel.pages.value.size > 1
                && viewModel.pages.value.remove(viewModel.showingPage.value)
            ) {
                viewModel.pages.notify()
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_repo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                val file = viewModel.showingFile ?: return true
                EditorActivity.start(this, editorLauncher, file.absolutePath)
                return true
            }
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

