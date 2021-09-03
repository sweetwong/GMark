package sweet.wong.sweetnote.filepreview

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.core.log
import sweet.wong.sweetnote.core.postDelayed
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.databinding.ActivityFilePreviewBinding
import sweet.wong.sweetnote.filepreview.markdown.MarkdownDelegate

class FilePreviewActivity : AppCompatActivity() {

    private val viewModel: FilePreviewViewModel by viewModels()

    private lateinit var binding: ActivityFilePreviewBinding
    private lateinit var markdown: MarkdownDelegate

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

        markdown = MarkdownDelegate(viewModel)

        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            viewModel.scrollY = scrollY
        }

        // 数据绑定
//        viewModel.path.observe(this) {
//            binding.drawerLayout.closeDrawer(binding.navigationView)
//            postDelayed(180) {
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
//            }
//        }

        viewModel.raw.observe(this) {
            markdown.setMarkdown(binding.markText, it)
            binding.drawerLayout.closeDrawer(binding.navigationView)
        }

        viewModel.selectFileEvent.observe(this) {
            changeFileAnim(0)
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
                val historyFile = historyStack.removeLast()
                viewModel.raw.value = historyFile.data
                viewModel.currentFile = historyFile.file
                changeFileAnim(historyFile.scrollY)
                log("history file", historyFile.file)
                return true
            }
        }

        return super.onKeyUp(keyCode, event)
    }

    private fun changeFileAnim(scrollY: Int) {
        binding.scrollView.visibility = View.INVISIBLE
        postDelayed(30) {
            TransitionManager.beginDelayedTransition(binding.scrollView)
            binding.scrollView.visibility = View.VISIBLE
            binding.scrollView.scrollY = scrollY
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
                Intent(context, FilePreviewActivity::class.java).putExtra(EXTRA_REPO, repo)
            )
        }

    }

}

