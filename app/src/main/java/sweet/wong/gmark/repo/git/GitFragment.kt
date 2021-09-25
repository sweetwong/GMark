package sweet.wong.gmark.repo.git

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.base.BaseFragment
import sweet.wong.gmark.databinding.FragmentGitBinding
import sweet.wong.gmark.repo.RepoViewModel

class GitFragment : BaseFragment<FragmentGitBinding>() {

    private val viewModel: GitViewModel by viewModels()
    private lateinit var repoViewModel: RepoViewModel
    private val adapter = DiffAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repoViewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]

        binding.rvGitDiff.adapter = adapter
//
//        viewModel.diffEntries.observe(viewLifecycleOwner) {
//            adapter.submitList(it.toMutableList())
//        }
//
//        viewModel.refresh(repoViewModel.rootFile)
    }

}