package sweet.wong.gmark.repo

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.LogUtils
import sweet.wong.gmark.databinding.FragmentRepoBinding
import sweet.wong.gmark.repo.markdown.MarkdownDelegate
import sweet.wong.gmark.utils.EventObserver
import sweet.wong.gmark.utils.SnappingLinearLayoutManager

class RepoFragment : Fragment() {

    private lateinit var binding: FragmentRepoBinding
    private lateinit var viewModel: RepoViewModel

    private lateinit var markdownDelegate: MarkdownDelegate

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        LogUtils.d("onCreateView")
        binding = FragmentRepoBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init markdown
        markdownDelegate = MarkdownDelegate(viewModel)

        initMarkList()
        initObservers()
    }

    private fun initMarkList() {
        binding.markList.layoutManager = SnappingLinearLayoutManager(requireContext())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.markList.isForceDarkAllowed = false
        }
        // Record current page scroll Y
        // Used for restore page
        binding.markList.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                viewModel.scrollY += dy
            }
        })
    }

    private fun initObservers() {
        viewModel.fileRaw.observe(viewLifecycleOwner) {
            markdownDelegate.setMarkdown(it.file.name, binding.markList, it.raw)
        }

        viewModel.selectFileEvent.observe(viewLifecycleOwner, EventObserver {
            scrollY(it.scrollY)
        })
    }

    /**
     * Restore scroll history
     */
    private fun scrollY(scrollY: Int) {
        // Scroll
        binding.markList.scrollBy(0, scrollY)

        // If it's new page, run animation
        if (scrollY == 0) {
            val animation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
            binding.markList.startAnimation(animation)
        }
    }

}