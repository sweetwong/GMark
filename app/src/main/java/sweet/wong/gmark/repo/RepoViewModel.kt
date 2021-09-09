package sweet.wong.gmark.repo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sweet.wong.gmark.core.io
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.core.ui
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.repo.history.Page
import sweet.wong.gmark.repo.project.ProjectUIState
import sweet.wong.gmark.utils.Event
import sweet.wong.gmark.utils.LimitedDeque
import java.io.File

class RepoViewModel : ViewModel() {

    /**
     * File raw text, this data may be large
     */
    val fileRaw = MutableLiveData<FileRaw>()

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

    var currentPagePosition = 0

    var tabLimit = 5

    var livingTab = 0

    val pages = mutableListOf<Page>()

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

    private fun selectFile(page: Page, pushToHistoryStack: Boolean = true) {
        val file = page.file

        if (!file.exists() || !file.isFile) {
            return toast("File $file is not exist")
        }

        if (file == showingFile) {
            drawerShowEvent.value = Event(false)
            return log("Repeat selection")
        }

        io {
            try {
                val raw = file.readText()
                ui {
                    onPageReady(page, raw, pushToHistoryStack)
                }
            } catch (e: Exception) {
                toast("Read file failed", e)
            }
        }
    }

    private fun onPageReady(page: Page, raw: String, pushToHistoryStack: Boolean) {
        val file = page.file

        val oldData = fileRaw.value
        val oldFile = showingFile
        if (oldData != null && oldFile != null && pushToHistoryStack) {
            savePage(Page(oldFile, scrollY), file)
        }

        fileRaw.value = FileRaw(file, raw)
        showingFile = file
        selectFileEvent.value = Event(page)
        scrollY = page.scrollY
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