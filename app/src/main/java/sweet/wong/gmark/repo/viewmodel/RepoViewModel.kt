package sweet.wong.gmark.repo.viewmodel

import androidx.databinding.ObservableArrayList
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
import java.io.File

class RepoViewModel : ViewModel() {

    /**
     * File raw text, this data may be large
     */
    val fileRaw = MutableLiveData<FileRaw>()

    val drawerShowEvent = MutableLiveData<Event<Boolean>>()

    val showingPage = MutableLiveData<Page>()

    val updateDrawerEvent = MutableLiveData<Event<ProjectUIState>>()

    val pages = ObservableArrayList<Page>()

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
        get() = showingPage.value?.file

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
                    showingPage.value = page
                }
            } catch (e: Exception) {
                toast("Read file failed", e)
            }
        }
    }

    fun removeShowingPage(): Boolean = isPositionValid() && pages.remove(showingPage.value)

    private fun isPositionValid() = currentTabPosition != -1 && currentTabPosition < pages.size

}