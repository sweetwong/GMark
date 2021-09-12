package sweet.wong.gmark.repo.outline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.databinding.FragmentOutlineBinding
import sweet.wong.gmark.repo.viewmodel.MarkdownViewModel

class OutlineFragment : Fragment() {

    private lateinit var binding: FragmentOutlineBinding
    private lateinit var adapter: OutlineAdapter

    private lateinit var markdownViewModel: MarkdownViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOutlineBinding.inflate(inflater, container, false)
        markdownViewModel = ViewModelProvider(requireActivity())[MarkdownViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = OutlineAdapter(markdownViewModel)
        binding.outlineList.adapter = adapter

        markdownViewModel.showingHeads.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

}