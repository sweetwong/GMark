package sweet.wong.gmark.repo.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.base.BaseFragment
import sweet.wong.gmark.databinding.FragmentHisotryBinding
import sweet.wong.gmark.repo.RepoViewModel

class HistoryFragment : BaseFragment<FragmentHisotryBinding>() {

    private lateinit var adapter: HistoryAdapter
    private val viewModel: HistoryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repoViewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]
        adapter = HistoryAdapter(viewModel)
        binding.historyList.adapter = adapter

        viewModel.histories.observe(viewLifecycleOwner) {
            adapter.submitList(it.toMutableList())
        }

        viewModel.repoViewModel.onDrawerShow.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.refresh()
            }
        }
    }

}