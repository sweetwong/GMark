package sweet.wong.gmark.search

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.doOnTextChanged
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.databinding.ActivitySearchBinding
import sweet.wong.gmark.utils.SinglePost

class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    private val singlePost = SinglePost()

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var fileSearchAdapter: FileSearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = intent.getStringExtra(EXTRA_ROOT)
        val path = intent.getStringExtra(EXTRA_PATH)
        if (root == null || path == null) {
            finish()
            return
        }

        viewModel.init(root, path)
        binding.initView()
    }

    private fun ActivitySearchBinding.initView() {
        etUrl.transitionName = SHARED_ELEMENT_NAME
        etUrl.doOnTextChanged { text, _, _, _ ->
            val textString = text?.toString().orEmpty()
            viewModel.keyword.value = textString
            singlePost.postDelayed(200) {
                viewModel.searchFileDelayed(textString)
                binding.rvSearchList.scrollToPosition(0)
            }
        }

        fileSearchAdapter = FileSearchAdapter(this@SearchActivity, viewModel)
        rvSearchList.adapter = fileSearchAdapter
        viewModel.fileSearchResult.observe(this@SearchActivity) {
            fileSearchAdapter.submitList(it.toMutableList())
        }
    }

    companion object {

        private const val SHARED_ELEMENT_NAME = "search_edit_text"
        private const val EXTRA_ROOT = "extra_root"
        private const val EXTRA_PATH = "extra_path"

        fun start(activity: Activity, view: View, root: String, path: String) {
            view.transitionName = SHARED_ELEMENT_NAME

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                view,
                SHARED_ELEMENT_NAME
            )

            val intent = Intent(activity, SearchActivity::class.java)
                .putExtra(EXTRA_ROOT, root)
                .putExtra(EXTRA_PATH, path)

            activity.startActivity(
                intent,
                options.toBundle()
            )
        }
    }

}