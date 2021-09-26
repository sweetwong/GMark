package sweet.wong.gmark.repo.project

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import sweet.wong.gmark.R
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.FragmentProjectBinding
import sweet.wong.gmark.repo.drawer.DrawerFragment

class ProjectFragment : DrawerFragment<FragmentProjectBinding>() {

    private val viewModel: ProjectViewModel by viewModels()

    private lateinit var browserAdapter: FileBrowserAdapter

    private lateinit var navBarAdapter: NavigationBarAdapter

    override val menuRes: Int
        get() = R.menu.menu_project

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        browserAdapter = FileBrowserAdapter(viewModel, viewLifecycleOwner) { selected ->
            if (selected.isNavigateBack) {
                val parent = ProjectUIState(
                    selected, selected.drawerFile.parentFile ?: return@FileBrowserAdapter
                )
                viewModel.selectDrawerFile(parent)
                return@FileBrowserAdapter
            }
            if (selected.drawerFile.isDirectory) {
                viewModel.selectDrawerFile(selected)
                return@FileBrowserAdapter
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
            viewModel.selectDrawerFile(it)
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

    override fun onMenuItemSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.menu_add -> {
                toast("Add")
            }
        }
    }

}