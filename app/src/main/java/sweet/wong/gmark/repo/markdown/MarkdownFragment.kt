package sweet.wong.gmark.repo.markdown

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.gmark.databinding.FragmentMarkdownBinding
import sweet.wong.gmark.repo.viewmodel.RepoViewModel
import sweet.wong.gmark.utils.SnappingLinearLayoutManager

/**
 * Fragment for markdown text preview
 */
class MarkdownFragment : Fragment() {

    private lateinit var binding: FragmentMarkdownBinding
    private lateinit var viewModel: RepoViewModel
    private lateinit var markdownDelegate: MarkdownDelegate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMarkdownBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init markdown
        markdownDelegate = MarkdownDelegate(viewModel)

        initMarkList()
        initObserver()
    }

    private fun initMarkList() {
        binding.markList.itemAnimator = null
        binding.markList.layoutManager = SnappingLinearLayoutManager(requireContext())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.markList.isForceDarkAllowed = false
        }
        // Record current page scroll Y
        // Used for restore page
        binding.markList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = viewModel.currentTabPosition
                val tabs = viewModel.pages
                if (position != -1 && position < tabs.size) {
                    tabs[viewModel.currentTabPosition].scrollY += dy
                }
            }
        })
    }

    private fun initObserver() {
        viewModel.fileRaw.observe(viewLifecycleOwner) {
            if (it.empty) {
                markdownDelegate.setMarkdown("README.md", binding.markList, "")
            } else {
                markdownDelegate.setMarkdown(it.file.name, binding.markList, it.raw)
            }
        }

        viewModel.showingPage.observe(viewLifecycleOwner) { page ->
            val scrollY = page.scrollY
            page.scrollY = 0
            scrollY(scrollY)
        }

    }

    /**
     * Restore scroll history
     */
    private fun scrollY(scrollY: Int) {
        // Scroll
        binding.markList.scrollBy(0, scrollY)

        val animation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
        binding.markList.startAnimation(animation)
    }

}