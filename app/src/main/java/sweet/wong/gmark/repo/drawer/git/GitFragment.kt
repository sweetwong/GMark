package sweet.wong.gmark.repo.drawer.git

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import sweet.wong.gmark.databinding.FragmentGitBinding
import sweet.wong.gmark.repo.drawer.DrawerFragment

class GitFragment : DrawerFragment<FragmentGitBinding>() {

    private val viewModel: GitViewModel by viewModels()
    private lateinit var adapter: DiffAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewBinding()
        initDiffList()
    }

    private fun initViewBinding() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.repo = repoViewModel.repo
        viewModel.repoViewModel = repoViewModel
    }

    private fun initDiffList() {
        adapter = DiffAdapter(viewModel, viewLifecycleOwner)
        binding.rvDiff.adapter = adapter
        binding.rvDiff.itemAnimator = null

        viewModel.diffUIStates.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        repoViewModel.onDrawerShow.observe(viewLifecycleOwner) { show ->
            if (show) {
                viewModel.refreshDiffList()
            }
        }
    }

}