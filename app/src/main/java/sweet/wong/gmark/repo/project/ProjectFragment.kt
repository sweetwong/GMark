package sweet.wong.gmark.repo.project

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.base.BaseFragment
import sweet.wong.gmark.databinding.FragmentProjectBinding
import sweet.wong.gmark.repo.RepoViewModel

class ProjectFragment : BaseFragment<FragmentProjectBinding>() {

    private val viewModel: ProjectViewModel by viewModels()
    private lateinit var repoViewModel: RepoViewModel

    private lateinit var uiState: ProjectUIState

    private lateinit var browserAdapter: FileBrowserAdapter

    private lateinit var navBarAdapter: NavigationBarAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repoViewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]

        browserAdapter = FileBrowserAdapter(viewModel, viewLifecycleOwner) { selected ->
            if (selected.navigateBack) {
                val parent = ProjectUIState(
                    selected, selected.drawerFile.parentFile ?: return@FileBrowserAdapter
                )
                viewModel.selectDrawerFile(parent)
                return@FileBrowserAdapter
            }
            if (selected.drawerFile.isDirectory) {
                viewModel.selectDrawerFile(selected)
            }
            if (selected.drawerFile.isFile) {
                repoViewModel.selectFile(selected.drawerFile)
            }
        }

        navBarAdapter = NavigationBarAdapter { selected ->
            viewModel.selectDrawerFile(selected)
        }

        binding.fileBrowser.adapter = browserAdapter
        binding.fileBrowser.itemAnimator = null
        binding.navigationBar.adapter = navBarAdapter

        repoViewModel.drawerFolder.observe(viewLifecycleOwner) {
            uiState = it
            viewModel.updateNavigationBar(it)
            viewModel.updateFileBrowser(it)
        }

        viewModel.fileBrowserList.observe(viewLifecycleOwner) {
            browserAdapter.submitList(it)
        }

        viewModel.navBarList.observe(viewLifecycleOwner) {
            navBarAdapter.submitList(it) {
                binding.navigationBar.scrollToPosition(it.lastIndex)
            }
        }
    }

}