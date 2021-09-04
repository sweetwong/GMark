package sweet.wong.gmark.repo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import sweet.wong.gmark.core.Event
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.repo.history.HistoryFile
import sweet.wong.gmark.utils.LimitedDeque
import java.io.File

class RepoViewModel : ViewModel() {

    /**
     * File raw text, this data may be large
     */
    val raw = MutableLiveData<String>()

    /**
     * Current drawer project folder, must be directory not a file
     */
    val currentProjectFolder = MutableLiveData<File>()

    val projectChildFiles = currentProjectFolder.switchMap {
        MutableLiveData(getFileChildren(it))
    }

    val drawerEvent = MutableLiveData<Event<Boolean>>()

    val drawerTitle = MutableLiveData<String>()

    /**
     * Current Repository, this data is get from argument
     */
    var repo: Repo? = null

    /**
     * Current selected File, must be a file not a directory
     */
    var currentFile: File? = null

    var scrollY: Int = 0

    private val historyStack = LimitedDeque<HistoryFile>(3)

    val selectFileEvent = MutableLiveData<Event<File>>()

    fun init(repo: Repo) {
        this.repo = repo
        drawerTitle.value = repo.name

        val file = File(repo.localPath)
        if (file.exists()) {
            currentProjectFolder.value = file
        }

        file.listFiles()?.forEach {
            if (it.name == "README.md") {
                selectFile(it)
            }
        }
    }

    fun selectFile(file: File) {
        if (!file.exists() || !file.isFile) {
            return toast("File $file is not exist")
        }

        if (file.absolutePath == currentFile?.absolutePath) {
            drawerEvent.value = Event(false)
            return log("Repeat selection")
        }

        Observable.fromCallable { file.readText() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                val oldData = raw.value
                val oldFile = currentFile
                if (!oldData.isNullOrBlank() && oldFile != null) {
                    pushHistoryStack(HistoryFile(oldFile, oldData, scrollY), file)
                }

                raw.value = it
                currentFile = file
                scrollY = 0
                selectFileEvent.value = Event(file)
            }
            .doOnError {
                toast("Read text failed", it)
            }
            .subscribe()
    }

    private fun pushHistoryStack(historyFile: HistoryFile, selectedFile: File) {
        // First add to history stack
        historyStack.add(historyFile)

        // Then remove duplicated history stack
        var toRemoved: HistoryFile? = null
        historyStack.forEach {
            if (it.file.absolutePath == selectedFile.absolutePath) {
                toRemoved = it
            }
        }
        toRemoved?.let { historyStack.remove(it) }
    }

    fun popHistoryStack(): HistoryFile? {
        if (historyStack.isNotEmpty()) {
            return historyStack.removeLast()?.apply {
                raw.value = data
                currentFile = file
            }
        }
        return null
    }

    private fun getFileChildren(folderFile: File): List<File> {
        if (!folderFile.exists() || !folderFile.isDirectory) return emptyList()

        val fileChildren = folderFile.listFiles()?.toMutableList() ?: return emptyList()

        val sorted = mutableListOf<File>()
        // First traversal add directories
        fileChildren.forEach {
            if (it.exists() && it.isDirectory && !filterFolder(it)) {
                sorted.add(it)
            }
        }
        // Second traversal add files
        fileChildren.forEach {
            if (it.exists() && it.isFile) {
                sorted.add(it)
            }
        }

        return sorted
    }

    /**
     * TODO: 2021/9/4 Add configuration plugin here
     */
    private fun filterFolder(file: File): Boolean {
        if (file.exists() && file.isDirectory && file.name.equals(".git")) {
            return true
        }
        return false
    }

    private fun filterFile(file: File): Boolean {
        if (file.exists() && file.isDirectory && file.name.equals(".git")) {
            return true
        }
        return false
    }

}