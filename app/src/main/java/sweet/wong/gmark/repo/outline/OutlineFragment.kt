package sweet.wong.gmark.repo.outline

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.R
import sweet.wong.gmark.databinding.FragmentOutlineBinding
import sweet.wong.gmark.repo.drawer.DrawerFragment
import sweet.wong.gmark.repo.markdown.MarkdownViewModel

class OutlineFragment : DrawerFragment<FragmentOutlineBinding>() {

    private lateinit var adapter: OutlineAdapter

    private lateinit var markdownViewModel: MarkdownViewModel

    override val menuRes: Int
        get() = R.menu.menu_editor

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