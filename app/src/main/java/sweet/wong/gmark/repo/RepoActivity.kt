package sweet.wong.gmark.repo

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.ScreenUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import sweet.wong.gmark.R
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.core.noOpDelegate
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.databinding.ActivityRepoBinding
import sweet.wong.gmark.editor.EditorActivity
import sweet.wong.gmark.ext.start
import sweet.wong.gmark.repo.drawer.DrawerDelegate
import sweet.wong.gmark.repolist.RepoListActivity
import sweet.wong.gmark.search.SearchActivity
import sweet.wong.gmark.settings.SettingsActivity
import sweet.wong.gmark.sp.SPUtils.settings
import sweet.wong.gmark.utils.EventObserver
import sweet.wong.gmark.utils.SearchUtils
import java.io.File
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

    private val searchLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val path = result.data?.data?.toString() ?: return@registerForActivityResult

            // Open url in WebView
            if (RegexUtils.isURL(path)) {
                viewModel.selectUrl(path)
                return@registerForActivityResult
            }

            try {
                val file = File(path)
                if (file.exists() && file.isFile) {
                    // Select local file
                    viewModel.selectFile(file)
                    return@registerForActivityResult
                }
            } catch (e: Exception) {
            }

            // Search keyword in WebView
            viewModel.selectUrl(SearchUtils.getSearchUrlByPref(path))
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

        initToolbar(viewModel.repo.name)
        initViewPager()
        initDrawer(savedInstanceState)
        initFab()

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

    private fun initToolbar(title: String) {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = title

        binding.tvUrl.setOnClickListener {
            SearchActivity.start(
                this,
                binding.tvUrl,
                searchLauncher,
                viewModel.repo.root,
                ""
            )
        }
    }

    private lateinit var pageAdapter: RepoPageAdapter
    private fun initViewPager() {
        pageAdapter = RepoPageAdapter(this, viewModel)
        binding.viewPager.adapter = pageAdapter
        viewModel.pages.observe(this) { pages ->
            pageAdapter.submitList(pages.toMutableList()) {
                if (viewModel.currentPosition != -1) {
                    binding.viewPager.currentItem = viewModel.currentPosition
                }
            }
        }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.currentPosition = position

                val showingPage = viewModel.showingPage ?: return
                when (showingPage.type) {
                    Page.TYPE_URL -> {
                        binding.tvUrl.text = showingPage.path
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            binding.tvUrl.tooltipText = showingPage.path
                        }

                        binding.tvUrl.ellipsize = TextUtils.TruncateAt.END

                        binding.fabEdit.isVisible = false
                    }
                    Page.TYPE_FILE -> {
                        val rootParent = viewModel.rootFile.parentFile ?: return
                        val relativePath = showingPage.file?.relativeTo(rootParent)?.path ?: return
                        binding.tvUrl.text = relativePath
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            binding.tvUrl.tooltipText = relativePath
                        }

                        binding.tvUrl.ellipsize = TextUtils.TruncateAt.START

                        binding.fabEdit.isVisible = true
                    }
                }
            }
        })

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            val textView = layoutInflater.inflate(
                R.layout.custom_tab_view, tab.view, false
            ) as TextView
            textView.setOnClickListener {
                binding.tabLayout.selectTab(tab)
            }
            tab.customView = textView

            val page = viewModel.pages.value[position]
            val path = when (page.type) {
                Page.TYPE_URL -> page.path
                else -> page.file?.name
            }
            textView.text = path
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textView.tooltipText = path
            }
        }.attach()

        viewModel.webViewNameUpdateEvent.observe(this, EventObserver {
            val tab = binding.tabLayout.getTabAt(viewModel.currentPosition) ?: return@EventObserver
            val textView = tab.customView as? TextView ?: return@EventObserver
            textView.text = it
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                textView.tooltipText = it
            }
            textView.requestLayout()
            binding.tabLayout.selectTab(tab)
            binding.tvUrl.text = it
        })

        binding.tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener by noOpDelegate() {

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    viewModel.onTabReselect.value = Unit
                }

            })
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

    private fun initFab() {
        binding.fabEdit.setOnClickListener {
            val file = viewModel.showingFile ?: return@setOnClickListener
            EditorActivity.start(this, editorLauncher, file.absolutePath)
        }
    }

    /**
     * Intercept back pressed
     */
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        // Pass event to view holders so that web view can handle key event
        if (pageAdapter.onKeyUp(keyCode, event)) {
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.drawerLayout.isDrawerVisible(binding.navigationView)) {
                binding.drawerLayout.closeDrawer(binding.navigationView)
                return true
            }

            // FIXME: 2021/10/1 这里有时候会判断错误
            if (viewModel.pageSize() > 1 && viewModel.removeShowingPage()) {
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

