package sweet.wong.sweetnote.repodetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.data.Repo
import java.io.File

class RepoViewerViewModel : ViewModel() {

    val repo = MutableLiveData<Repo>()

    /**
     * Point to current selected File, must be a file not a directory
     */
    val currentFile = MutableLiveData<File>()

    val fileText = currentFile.switchMap {
        MutableLiveData(it.readText())
    }

    /**
     * Point to current drawer project folder, must be directory not a file
     */
    val currentProjectFolder = MutableLiveData<File>()

    val projectChildFiles = currentProjectFolder.switchMap {
        MutableLiveData(getFileList(it))
    }

    private fun getFileList(folderFile: File): List<File> {
        if (!folderFile.exists()) return emptyList()
        if (!folderFile.isDirectory) return emptyList()

        val childFiles = mutableListOf<File>()
        folderFile.listFiles()?.forEach {
            childFiles.add(it)
        }

        return childFiles
    }

}