package sweet.wong.gmark.search

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.doOnTextChanged
import com.blankj.utilcode.util.KeyboardUtils
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.core.delay
import sweet.wong.gmark.databinding.ActivitySearchBinding
import sweet.wong.gmark.utils.EventObserver
import sweet.wong.gmark.utils.SinglePost

class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    private val singlePost = SinglePost()

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var fileSearchAdapter: FileSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.sharedElementEnterTransition.duration = 200
        window.sharedElementReturnTransition.duration = 200

        val root = intent.getStringExtra(EXTRA_ROOT)
        val path = intent.getStringExtra(EXTRA_PATH)
        if (root == null || path == null) {
            finish()
            return
        }

        viewModel.init(root, path)
        binding.initView()

        delay(500) {
            KeyboardUtils.showSoftInput(binding.etSearch)
        }

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            val intent = Intent().setData(Uri.parse(v.text.toString()))
            setResult(Activity.RESULT_OK, intent)
            finish()
            overridePendingTransition(0, 0)
            true
        }
    }

    private fun ActivitySearchBinding.initView() {
        etSearch.transitionName = SHARED_ELEMENT_NAME
        etSearch.doOnTextChanged { text, _, _, _ ->
            val textString = text?.toString().orEmpty().trim()
            viewModel.keyword.value = textString
            singlePost.postDelayed(200) {
                viewModel.searchFileDelayed(textString)
            }
        }

        fileSearchAdapter = FileSearchAdapter(this@SearchActivity, viewModel)
        rvSearchList.adapter = fileSearchAdapter
        viewModel.fileSearchResult.observe(this@SearchActivity) {
            fileSearchAdapter.submitList(it.toMutableList()) {
                binding.rvSearchList.scrollToPosition(0)
            }
        }

        viewModel.selectFileEvent.observe(this@SearchActivity, EventObserver {
            val intent = Intent().setData(Uri.parse(it.absolutePath))
            setResult(Activity.RESULT_OK, intent)
            finish()
            overridePendingTransition(0, 0)
        })
    }

    companion object {

        private const val SHARED_ELEMENT_NAME = "search_edit_text"
        private const val EXTRA_ROOT = "extra_root"
        private const val EXTRA_PATH = "extra_path"

        fun start(
            activity: Activity,
            view: View,
            launcher: ActivityResultLauncher<Intent>,
            root: String,
            path: String
        ) {
            view.transitionName = SHARED_ELEMENT_NAME

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                view,
                SHARED_ELEMENT_NAME
            )

            val intent = Intent(activity, SearchActivity::class.java)
                .putExtra(EXTRA_ROOT, root)
                .putExtra(EXTRA_PATH, path)

            launcher.launch(intent, options)
        }
    }

}