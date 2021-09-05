package sweet.wong.gmark.repo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import sweet.wong.gmark.core.Event
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.repo.drawer.history.HistoryFile
import sweet.wong.gmark.repo.drawer.project.ProjectUIState
import sweet.wong.gmark.utils.LimitedDeque
import java.io.File

class RepoViewModel : ViewModel() {

    /**
     * File raw text, this data may be large
     */
    val rawText = MutableLiveData<String>()

    val drawerShowEvent = MutableLiveData<Event<Boolean>>()

    val selectFileEvent = MutableLiveData<Event<File>>()

    val drawerTitle = MutableLiveData<String>()

    val updateDrawerEvent = MutableLiveData<Event<ProjectUIState>>()

    /**
     * Current Repository, this data is get from argument
     */
    lateinit var repo: Repo

    lateinit var rootFile: File

    /**
     * Current selected File, must be a file not a directory
     */
    var currentFile: File? = null

    var scrollY: Int = 0

    private val historyStack = LimitedDeque<HistoryFile>(3)

    fun init(repo: Repo) {
        this.repo = repo
        rootFile = File(repo.localPath)

        drawerTitle.value = repo.name

        val file = File(repo.localPath)

        file.listFiles()?.forEach {
            if (it.name == "README.md") {
                selectFile(it)
            }
        }
    }

    fun updateDrawer() {
        val currentFile = this.currentFile ?: return
        val drawerFile = currentFile.parentFile ?: return

        val uiState = ProjectUIState(drawerFile, currentFile, rootFile)
        updateDrawerEvent.value = Event(uiState)
    }

    fun selectFile(file: File, pushToHistoryStack: Boolean = true) {
        if (!file.exists() || !file.isFile) {
            return toast("File $file is not exist")
        }

        if (file.absolutePath == currentFile?.absolutePath) {
            drawerShowEvent.value = Event(false)
            return log("Repeat selection")
        }

        Observable.fromCallable { file.readText() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                val oldData = rawText.value
                val oldFile = currentFile
                if (!oldData.isNullOrBlank() && oldFile != null && pushToHistoryStack) {
                    pushToHistoryStack(HistoryFile(oldFile, oldData, scrollY), file)
                }

                rawText.value = it
                currentFile = file
                scrollY = 0
                selectFileEvent.value = Event(file)
            }
            .doOnError {
                toast("Read text failed", it)
            }
            .subscribe()
    }

    private fun pushToHistoryStack(historyFile: HistoryFile, selectedFile: File) {
        // First add to history stack
        historyStack.add(historyFile)

        // Then remove duplicated history stack
        var toRemoved: HistoryFile? = null
        historyStack.forEach {
            if (it.file == selectedFile) {
                toRemoved = it
            }
        }
        toRemoved?.let { historyStack.remove(it) }
    }

    fun popHistoryStack(): HistoryFile? {
        if (historyStack.isEmpty()) {
            return null
        }

        val historyFile = historyStack.removeLast()
        historyFile?.let { selectFile(it.file, false) }
        return historyFile
    }

}