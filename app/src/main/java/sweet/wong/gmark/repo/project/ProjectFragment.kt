package sweet.wong.gmark.repo.project

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.base.BaseFragment
import sweet.wong.gmark.databinding.FragmentProjectBinding
import sweet.wong.gmark.repo.RepoViewModel
import java.io.File

class ProjectFragment : BaseFragment<FragmentProjectBinding>() {

    private lateinit var viewModel: RepoViewModel

    private lateinit var uiState: ProjectUIState

    private val browserAdapter = FileBrowserAdapter {
        if (it.navigateBack) {
            val parent = ProjectUIState(
                it, it.drawerFile.parentFile ?: return@FileBrowserAdapter
            )
            updateFileBrowser(parent)
            updateNavigationBar(parent)
            return@FileBrowserAdapter
        }
        if (it.drawerFile.isDirectory) {
            updateFileBrowser(it)
            updateNavigationBar(it)
        }
        if (it.drawerFile.isFile) {
            viewModel.selectFile(it.drawerFile)
        }
    }

    private val barAdapter = NavigationBarAdapter {
        updateFileBrowser(it)
        updateNavigationBar(it)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]

        binding.fileBrowser.adapter = browserAdapter
        binding.fileBrowser.itemAnimator = null
        binding.navigationBar.adapter = barAdapter

        viewModel.drawerFolder.observe(viewLifecycleOwner) {
            uiState = it
            updateNavigationBar(it)
            updateFileBrowser(it)
        }
    }

    private fun updateNavigationBar(uiState: ProjectUIState) {
        val list = mutableListOf<ProjectUIState>()
        var file: File? = uiState.drawerFile
        // If has showing file, add to navigation bar end
        var hasShowingFile = false
        file?.listFiles()?.forEach {
            if (it == uiState.showingFile) {
                hasShowingFile = true
            }
        }
        if (hasShowingFile) {
            list.add(ProjectUIState(uiState.showingFile, uiState.showingFile, uiState.rootFile))
        }
        // Add traversal parent file, then should navigation bar
        while (file != null) {
            list.add(ProjectUIState(file, uiState.showingFile, uiState.rootFile))
            if (file == uiState.rootFile) {
                break
            }
            file = file.parentFile
        }
        barAdapter.submitList(list.reversed()) {
            binding.navigationBar.scrollToPosition(list.lastIndex)
        }
    }


    private fun updateFileBrowser(uiState: ProjectUIState) {
        val folderFile = uiState.drawerFile

        if (!folderFile.exists() || !folderFile.isDirectory) return

        val fileChildren = folderFile.listFiles()?.toMutableList() ?: return

        val sorted = mutableListOf<ProjectUIState>()

        // First show back folder
        if (uiState.drawerFile != uiState.rootFile) {
            sorted.add(
                ProjectUIState(uiState.drawerFile, uiState.showingFile, uiState.rootFile, true)
            )
        }

        // Second traversal add directories
        fileChildren.forEach {
            if (it.exists() && it.isDirectory && !filterFolder(it)) {
                sorted.add(ProjectUIState(it, uiState.showingFile, uiState.rootFile))
            }
        }
        // Third traversal add files
        fileChildren.forEach {
            if (it.exists() && it.isFile) {
                sorted.add(ProjectUIState(it, uiState.showingFile, uiState.rootFile))
            }
        }

        browserAdapter.submitList(sorted)
    }

    private fun filterFolder(file: File): Boolean {
        if (file.exists() && file.isDirectory && file.name.equals(".git")) {
            return true
        }
        return false
    }


}