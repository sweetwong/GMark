//package sweet.wong.gmark.repo.markdown
//
//import android.os.Build
//import android.os.Bundle
//import android.view.View
//import android.view.animation.AnimationUtils
//import androidx.lifecycle.ViewModelProvider
//import androidx.recyclerview.widget.RecyclerView
//import sweet.wong.gmark.base.BaseFragment
//import sweet.wong.gmark.databinding.FragmentMarkdownBinding
//import sweet.wong.gmark.repo.RepoViewModel
//import sweet.wong.gmark.utils.SnappingLinearLayoutManager
//
///**
// * Fragment for markdown text preview
// */
//class MarkdownFragment : BaseFragment<FragmentMarkdownBinding>() {
//
//    private lateinit var repoViewModel: RepoViewModel
//    private lateinit var markdownDelegate: MarkdownDelegate
//    private lateinit var markdownViewModel: MarkdownViewModel
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        repoViewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]
//        markdownViewModel = ViewModelProvider(requireActivity())[MarkdownViewModel::class.java]
//        binding.viewModel = repoViewModel
//        binding.lifecycleOwner = this
//
//        // Init markdown
//        markdownDelegate = MarkdownDelegate(repoViewModel, markdownViewModel)
//
//        initMarkList()
//        initObserver()
//    }
//
//    private fun initMarkList() {
//        binding.markList.itemAnimator = null
//        binding.markList.layoutManager = SnappingLinearLayoutManager(requireContext())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            binding.markList.isForceDarkAllowed = false
//        }
//        // Record current page scroll Y
//        // Used for restore page
//        binding.markList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                val position = repoViewModel.currentTabPosition
//                val tabs = repoViewModel.pages
//                if (position != -1 && position < tabs.size) {
//                    tabs[repoViewModel.currentTabPosition].scrollY += dy
//                }
//            }
//        })
//    }
//
//    private fun initObserver() {
//        repoViewModel.fileRaw.observe(viewLifecycleOwner) {
//            if (it.empty) {
//                markdownDelegate.setMarkdown("README.md", binding.markList, "")
//            } else {
//                markdownDelegate.setMarkdown(it.file.name, binding.markList, it.raw)
//            }
//        }
//
//        repoViewModel.showingPage.observe(viewLifecycleOwner) { page ->
//            val scrollY = page.scrollY
//            page.scrollY = 0
//            scrollY(scrollY)
//        }
//
//    }
//
//    /**
//     * Restore scroll history
//     */
//    private fun scrollY(scrollY: Int) {
//        // Scroll
//        binding.markList.scrollBy(0, scrollY)
//
//        val animation = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)
//        binding.markList.startAnimation(animation)
//    }
//
//}