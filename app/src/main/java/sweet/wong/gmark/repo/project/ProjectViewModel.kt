package sweet.wong.gmark.repo.project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.core.toast
import java.io.File

class ProjectViewModel : ViewModel() {

    val navBarList = MutableLiveData<List<ProjectUIState>>()
    val fileBrowserList = MutableLiveData<List<ProjectUIState>>()

    private var currentUIState: ProjectUIState? = null

    fun selectDrawerFile(uiState: ProjectUIState) {
        currentUIState = uiState
        updateFileBrowser(uiState)
        updateNavigationBar(uiState)
    }

    fun refreshDrawer() {
        currentUIState?.let {
            selectDrawerFile(it)
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

        navBarList.value = list.reversed()
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

        fileBrowserList.value = sorted
    }

    private fun filterFolder(file: File): Boolean {
        if (file.exists() && file.isDirectory && file.name.equals(".git")) {
            return true
        }
        return false
    }

    fun addNew() {

    }

    fun new() {

    }

    fun rename(file: File, newName: String) = liveData(Dispatchers.IO) {
        try {
            // Same name, do nothing, and callback success
            if (file.name == newName) {
                return@liveData
            }
            // Check blank
            if (newName.isBlank()) {
                toast("File name should not be blank")
                return@liveData
            }
            // Parent doesn't exist, callback fail
            val parentFile = file.parentFile
            if (parentFile?.exists() != true) {
                return@liveData
            }
            // New name exists, callback fail
            val newFile = File(parentFile, newName)
            if (newFile.exists()) {
                toast("File exists, please delete old file first")
                return@liveData
            }
            // Rename
            if (file.renameTo(newFile)) {
                emit(Unit)
            } else {
                toast("Rename file failed")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Rename file failed", e)
        }
    }

    fun delete(uiState: ProjectUIState) = viewModelScope.launch(Dispatchers.IO) {
        try {
            if (uiState.drawerFile.deleteRecursively()) {
                withContext(Dispatchers.Main) { refreshDrawer() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Delete failed", e)
        }
    }

}