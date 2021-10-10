package sweet.wong.gmark.diff

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.ActivityDiffBinding
import sweet.wong.gmark.ext.start

class DiffActivity : BaseActivity<ActivityDiffBinding>() {

    private val viewModel: DiffViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val path = intent.getStringExtra(EXTRA_DIFF_PATH)
        val root = intent.getStringExtra(EXTRA_ROOT)
        if (path.isNullOrEmpty() || root.isNullOrEmpty()) {
            toast("Path or root is empty")
            finish()
            return
        }
        viewModel.path = path
        viewModel.root = root

        binding.viewModel = viewModel

        initToolbar(binding.toolbar)

        viewModel.init()
    }

    companion object {

        private const val EXTRA_DIFF_PATH = "extra_diff_path"
        private const val EXTRA_ROOT = "extra_root"

        fun start(context: Context, root: String, path: String) {
            context.start<DiffActivity> {
                putExtra(EXTRA_ROOT, root)
                putExtra(EXTRA_DIFF_PATH, path)
            }
        }

    }

}