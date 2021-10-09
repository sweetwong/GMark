package sweet.wong.gmark.repo.viewholder

import android.os.Build
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.databinding.FragmentMarkdownBinding
import sweet.wong.gmark.ext.MAIN_CATCH
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.repo.RepoViewModel
import sweet.wong.gmark.repo.markdown.MarkdownDelegate
import sweet.wong.gmark.utils.EventObserver
import sweet.wong.gmark.utils.SnappingLinearLayoutManager

class MarkdownViewHolder(
    private val activity: AppCompatActivity,
    private val viewModel: RepoViewModel,
    private val binding: FragmentMarkdownBinding,
) : AbsViewHolder<Page>(binding.root) {

    private val delegate = MarkdownDelegate(viewModel)

    init {
        binding.markList.itemAnimator = null
        binding.markList.layoutManager = SnappingLinearLayoutManager(itemView.context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.markList.isForceDarkAllowed = false
        }
        viewModel.onTabReselect.observe(activity) {
            if (adapterPosition == viewModel.currentPosition) {
                binding.markList.smoothScrollToPosition(0)
            }
        }
        viewModel.updateFile.observe(activity, EventObserver {
            if (viewModel.currentPosition == adapterPosition) {
                viewModel.showingPage?.let { page ->
                    bind(page)
                }
            } else {
                viewModel.updateFile.value?.hasBeenHandled = false
            }
        })
    }

    override fun bind(data: Page) {
        LogUtils.d("MarkdownViewHolder bind page, position: $adapterPosition, page: $data")
        activity.lifecycleScope.launch(Dispatchers.MAIN_CATCH) {
            val file = data.file ?: return@launch
            val source = withContext(Dispatchers.IO) { file.readText() }
            delegate.setMarkdown(file.name, binding.markList, source)

            binding.markList.scrollBy(0, data.scrollY)

            data.scrollY = 0
            binding.markList.clearOnScrollListeners()
            binding.markList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    data.scrollY += dy
                }
            })

        }
    }

    companion object {

        fun create(activity: AppCompatActivity, viewModel: RepoViewModel, parent: ViewGroup) =
            MarkdownViewHolder(
                activity,
                viewModel,
                FragmentMarkdownBinding.inflate(parent.inflater, parent, false)
            )

    }

}
