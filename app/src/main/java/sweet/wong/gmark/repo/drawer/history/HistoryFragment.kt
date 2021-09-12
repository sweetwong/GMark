package sweet.wong.gmark.repo.drawer.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.databinding.FragmentHisotryBinding
import sweet.wong.gmark.repo.viewmodel.HistoryViewModel
import sweet.wong.gmark.repo.viewmodel.RepoViewModel

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHisotryBinding
    private lateinit var adapter: HistoryAdapter
    private val viewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHisotryBinding.inflate(inflater, container, false)
        viewModel.repoViewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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