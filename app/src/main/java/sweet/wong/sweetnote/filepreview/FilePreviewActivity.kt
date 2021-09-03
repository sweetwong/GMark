package sweet.wong.sweetnote.filepreview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.databinding.ActivityFilePreviewBinding

class FilePreviewActivity : AppCompatActivity() {

    private val viewModel: FilePreviewViewModel by viewModels()

    private lateinit var binding: ActivityFilePreviewBinding

    private lateinit var markwon: Markwon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repo = intent.getParcelableExtra<Repo>(EXTRA_REPO)
        if (repo == null) {
            finish()
            return
        }

        // 工具栏
        setSupportActionBar(binding.toolbar)
        binding.toolbar.isVisible = false

        // 抽屉栏
        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        actionBarDrawerToggle.drawerArrowDrawable.color = Color.WHITE
        actionBarDrawerToggle.syncState()

        // Markdown
        markwon = Markwon.builder(this)
            .usePlugins(listOf(GlideImagesPlugin.create(this), HtmlPlugin.create()))
            .build()

        // 数据绑定
//        viewModel.path.observe(this) {
//            binding.drawerLayout.closeDrawer(binding.navigationView)
//            postDelayed(180) {
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            }
//        }

        viewModel.fileText.observe(this) {
            markwon.setMarkdown(binding.markText, it)
            binding.drawerLayout.closeDrawer(binding.navigationView)
        }

        viewModel.init(repo)
    }

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
            val historyStack = viewModel.historyStack
            if (historyStack.isNotEmpty()) {
                setText(historyStack.removeLast())
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    private fun setText(md: String) {
        markwon.setMarkdown(binding.markText, md)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    companion object {

        private const val EXTRA_REPO = "extra_repo"

        fun start(context: Context, repo: Repo) {
            context.startActivity(
                Intent(context, FilePreviewActivity::class.java).putExtra(EXTRA_REPO, repo)
            )
        }

    }

}

