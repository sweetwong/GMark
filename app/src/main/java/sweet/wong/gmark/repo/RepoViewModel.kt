package sweet.wong.gmark.repo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.repo.drawer.history.Page
import sweet.wong.gmark.repo.drawer.project.ProjectUIState
import sweet.wong.gmark.utils.Event
import sweet.wong.gmark.utils.LimitedDeque
import java.io.File

class RepoViewModel : ViewModel() {

    /**
     * File raw text, this data may be large
     */
    val rawText = MutableLiveData<String>()

    val drawerShowEvent = MutableLiveData<Event<Boolean>>()

    val selectFileEvent = MutableLiveData<Event<Page>>()

    val updateDrawerEvent = MutableLiveData<Event<ProjectUIState>>()

    /**
     * Current Repository, this data is get from argument
     */
    lateinit var repo: Repo

    lateinit var rootFile: File

    /**
     * Current showing File, must be a file not a directory
     */
    var showingFile: File? = null

    var scrollY: Int = 0

    private val historyStack = LimitedDeque<Page>(3)

    fun init(repo: Repo) {
        this.repo = repo
        rootFile = File(repo.localPath)
        rootFile.listFiles()?.forEach {
            if (it.name == "README.md") {
                selectFile(it)
            }
        }
    }

    fun updateDrawer() {
        val showingFile = this.showingFile ?: return
        val drawerFile = showingFile.parentFile ?: return

        val uiState = ProjectUIState(drawerFile, showingFile, rootFile)
        updateDrawerEvent.value = Event(uiState)
    }

    fun selectFile(file: File, pushToHistoryStack: Boolean = true) {
        selectFile(Page(file, 0), pushToHistoryStack)
    }

    fun selectFile(page: Page, pushToHistoryStack: Boolean = true) {
        val file = page.file

        if (!file.exists() || !file.isFile) {
            return toast("File $file is not exist")
        }

        if (file == showingFile) {
            drawerShowEvent.value = Event(false)
            return log("Repeat selection")
        }

        Observable.fromCallable { file.readText() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                val oldData = rawText.value
                val oldFile = showingFile
                if (!oldData.isNullOrBlank() && oldFile != null && pushToHistoryStack) {
                    savePage(Page(oldFile, scrollY), file)
                }

                rawText.value = it
                showingFile = file
                selectFileEvent.value = Event(page)
                scrollY = page.scrollY
            }
            .doOnError {
                toast("Read text failed", it)
            }
            .subscribe()
    }

    fun restorePage(): Boolean {
        if (historyStack.isEmpty()) {
            return false
        }

        val historyFile = historyStack.removeLast()
        historyFile?.let { selectFile(it, false) }
        return true
    }

    private fun savePage(page: Page, selectedFile: File) {
        // First add to history stack
        historyStack.add(page)

        // Then remove duplicated history stack
        var toRemoved: Page? = null
        historyStack.forEach {
            if (it.file == selectedFile) {
                toRemoved = it
            }
        }
        toRemoved?.let { historyStack.remove(it) }
    }

}