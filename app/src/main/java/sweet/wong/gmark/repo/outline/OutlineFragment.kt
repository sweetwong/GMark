package sweet.wong.gmark.repo.outline

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.base.BaseFragment
import sweet.wong.gmark.databinding.FragmentOutlineBinding
import sweet.wong.gmark.repo.markdown.MarkdownViewModel

class OutlineFragment : BaseFragment<FragmentOutlineBinding>() {

    private lateinit var adapter: OutlineAdapter

    private lateinit var markdownViewModel: MarkdownViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markdownViewModel = ViewModelProvider(requireActivity())[MarkdownViewModel::class.java]
        adapter = OutlineAdapter(markdownViewModel)
        binding.outlineList.adapter = adapter

        markdownViewModel.showingHeads.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

}