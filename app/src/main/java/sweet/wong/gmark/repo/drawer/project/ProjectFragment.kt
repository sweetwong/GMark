package sweet.wong.gmark.repo.drawer.project

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import sweet.wong.gmark.core.EventObserver
import sweet.wong.gmark.databinding.FragmentProjectBinding
import sweet.wong.gmark.repo.RepoViewModel
import java.io.File

class ProjectFragment : Fragment() {

    private lateinit var viewModel: RepoViewModel
    private lateinit var binding: FragmentProjectBinding

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fileBrowser.adapter = browserAdapter
        binding.navigationBar.adapter = barAdapter

        viewModel.updateDrawerEvent.observe(viewLifecycleOwner, EventObserver {
            uiState = it
            updateNavigationBar(it)
            updateFileBrowser(it)
        })
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
        barAdapter.submitList(list.reversed())
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
        // Thrid traversal add files
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