package sweet.wong.gmark.repo.project

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.utils.Event
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

    fun updateNavigationBar(uiState: ProjectUIState) {
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

    fun updateFileBrowser(uiState: ProjectUIState) {
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

    fun renameFile(file: File, newName: String): MutableLiveData<Event<Boolean>> {
        val result = MutableLiveData<Event<Boolean>>()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Same name, do nothing, and callback success
                if (file.name == newName) {
                    result.postValue(Event(true))
                    return@launch
                }
                // Parent doesn't exist, callback fail
                val parentFile = file.parentFile
                if (parentFile?.exists() != true) {
                    result.postValue(Event(false))
                    return@launch
                }
                // New name exists, callback fail
                val newFile = File(parentFile, newName)
                if (newFile.exists()) {
                    result.postValue(Event(false))
                    toast("File exists, please delete old file first")
                    return@launch
                }
                // Rename
                file.renameTo(newFile)
                result.postValue(Event(true))
            } catch (e: Exception) {
                result.postValue(Event(false))
                toast("Rename file failed", e)
                e.printStackTrace()
            }
        }
        return result
    }

}