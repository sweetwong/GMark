package sweet.wong.gmark.repo.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sweet.wong.gmark.core.io
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.core.ui
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
    val fileRaw = MutableLiveData<FileRaw>()

    val drawerShowEvent = MutableLiveData<Event<Boolean>>()

    val selectFileEvent = MutableLiveData<Event<Page>>()

    val updateDrawerEvent = MutableLiveData<Event<ProjectUIState>>()

    val pages: ObservableList<Page> = ObservableArrayList()

    var currentTabPosition = -1

    /**
     * Current Repository, this data is get from argument
     */
    lateinit var repo: Repo

    lateinit var rootFile: File

    /**
     * Current showing File, must be a file not a directory
     */
    val showingFile: File?
        get() = showingPage?.file

    var showingPage: Page? = null

    private val historyStack = LimitedDeque<Page>(3)

    fun init(repo: Repo) {
        this.repo = repo
        rootFile = File(repo.localPath)
    }

    fun loadREADME() {
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

    fun selectFile(file: File) {
        selectPage(Page(file, 0))
    }

    fun selectPage(page: Page, newFile: Boolean = true) {
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
                    if (newFile) {
                        pages.add(page)
                    }
                    fileRaw.value = FileRaw(file, raw)
                    showingPage = page
                    selectFileEvent.value = Event(page)
                }
            } catch (e: Exception) {
                toast("Read file failed", e)
            }
        }
    }

    fun restorePage(): Boolean {
        if (historyStack.isEmpty()) {
            return false
        }

        val historyFile = historyStack.removeLast()
        historyFile?.let { selectPage(it, false) }
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