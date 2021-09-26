package sweet.wong.gmark.repo.git

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

        adapter = DiffAdapter(viewModel, viewLifecycleOwner)
        binding.rvGitDiff.adapter = adapter
        binding.rvGitDiff.itemAnimator = null

        viewModel.uiStates.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        repoViewModel.onDrawerShow.observe(viewLifecycleOwner) { show ->
            if (show) {
                viewModel.refresh(repoViewModel.rootFile)
            }
        }
    }

}