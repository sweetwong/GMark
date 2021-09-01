package sweet.wong.sweetnote

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.widget.ProgressBar
import android.widget.TextView
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
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import sweet.wong.sweetnote.core.postDelayed
import sweet.wong.sweetnote.drawer.ProjectDrawerView
import sweet.wong.sweetnote.event.TextUpdateEvent
import java.io.File

class MDViewActivity : AppCompatActivity() {

    private lateinit var currentPath: String

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerView: ProjectDrawerView
    private lateinit var navigationView: NavigationView
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var markwon: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        markwon = Markwon.builder(this)
            .usePlugins(listOf(GlideImagesPlugin.create(this), HtmlPlugin.create()))
            .build()

        parseParams()

        initView()
        initData()

        toolbar.isVisible = false
    }

    private fun parseParams() {
        currentPath = intent.extras?.getString(EXTRA_REPOSITORY_ROOT) ?: DEFAULT
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
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

    @Subscribe
    fun onTextUpdateEvent(event: TextUpdateEvent) {
        drawerLayout.closeDrawer(navigationView)
        postDelayed(180) {
            start(this, event.path)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun initView() {
        findViews()
        initToolbar()
        initDrawer()
        markwon.setMarkdown(textView, File(currentPath).readText())
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

        drawerView.currentMDPath = currentPath
    }

    private fun initData() {
//        CloneRepository.clone(object : ProgressMonitor by noOpDelegate() {})
    }

    companion object {

        fun start(context: Context, mdPath: String) {
            val intent = Intent(context, MDViewActivity::class.java).apply {
                putExtra(EXTRA_REPOSITORY_ROOT, mdPath)
            }
            context.startActivity(intent)
        }


        private const val EXTRA_REPOSITORY_ROOT = "extra_repository_root"

        private val DEFAULT = PathUtils.getExternalAppFilesPath() + "/QA/QA.md"

    }

}

