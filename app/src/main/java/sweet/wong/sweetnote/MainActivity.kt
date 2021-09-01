package sweet.wong.sweetnote

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
import com.blankj.utilcode.util.ToastUtils
import com.google.android.material.navigation.NavigationView
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.eclipse.jgit.lib.ProgressMonitor
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import sweet.wong.sweetnote.drawer.DrawerView
import sweet.wong.sweetnote.event.TextUpdateEvent
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerView: DrawerView
    private lateinit var navigationView: NavigationView
    private lateinit var textView: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        EventBus.getDefault().register(this)

        initView()
        initData()

        toolbar.isVisible = false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(navigationView)) {
                drawerView.onBackPressed()
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    @Subscribe
    fun onTextUpdateEvent(event: TextUpdateEvent) {
        Observable.fromCallable { File(event.path).readText() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                val markwon = Markwon.builder(this)
                    .usePlugins(listOf(GlideImagesPlugin.create(this), HtmlPlugin.create()))
                    .build()
                markwon.setMarkdown(textView, it)
                drawerLayout.closeDrawer(navigationView)
            }
            .doOnError {
                ToastUtils.showLong(it.toString())
            }
            .subscribe()
    }

    private fun initView() {
        findViews()
        initToolbar()
        initDrawer()
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
//        CloneRepository.clone(RepoCloneMonitor())
    }

    inner class RepoCloneMonitor : ProgressMonitor {

        override fun start(totalTasks: Int) {
            log("start", totalTasks)
        }

        override fun beginTask(title: String?, totalWork: Int) {
            log("beginTask", title, totalWork)
        }

        override fun update(completed: Int) {
            log("update", completed)
        }

        override fun endTask() {
            log("endTask")
        }

        override fun isCancelled(): Boolean {
            return false
        }
    }


}

