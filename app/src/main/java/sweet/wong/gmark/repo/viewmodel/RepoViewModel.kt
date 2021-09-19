package sweet.wong.gmark.repo.viewmodel

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import sweet.wong.gmark.core.io
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.core.ui
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.repo.data.FileRaw
import sweet.wong.gmark.repo.project.ProjectUIState
import sweet.wong.gmark.sp.SPConstant
import sweet.wong.gmark.sp.SPUtils
import sweet.wong.gmark.utils.Event
import java.io.File

class RepoViewModel : ViewModel() {

    /**
     * File raw text, this data may be large
     */
    val fileRaw = MutableLiveData<FileRaw>()

    /**
     * This is used to control drawer
     */
    val showDrawer = MutableLiveData<Event<Boolean>>()

    /**
     * This is drawer callback
     */
    val onDrawerShow = MutableLiveData<Boolean>()

    val showingPage = MutableLiveData<Page>()

    val drawerFolder = MutableLiveData<ProjectUIState>()

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

    fun init(): Boolean {
        val url = SPUtils.getString(SPConstant.LAST_REPO_URL)
        if (url.isNullOrEmpty()) {
            toast("Can not read repository uid", url)
            return false
        }

        val repo = DaoManager.repoDao.get(url)
        if (repo == null) {
            toast("Repo is null")
            return false
        }
        this.repo = repo

        rootFile = File(repo.localPath)
        if (!rootFile.exists() || !rootFile.isDirectory) {
            toast("Repository root path is invalid")
            return false
        }

        return true
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
        drawerFolder.value = uiState
    }

    fun selectFile(position: Int) {
        if (position !in 0 until pages.size) {
            toast("Select file failed cause position is invalid")
            return
        }
        selectFile(pages[position])
    }

    fun selectFile(page: Page) {
        selectFile(page.file)
    }

    fun selectFile(path: String) {
        selectFile(File(path))
    }

    /**
     * There are three cases:
     *
     * 1. Choose same file
     * 2. Choose existing file
     * 3. Choose new file
     */
    fun selectFile(file: File?, forceUpdate: Boolean = false) {
        // Check file valid
        if (file == null || !file.exists() || !file.isFile) {
            return toast("File $file is not exist")
        }

        // Check Same file
        if (file == showingFile && !forceUpdate) {
            // Close and refresh drawer
            showDrawer.value = Event(false)
            updateDrawer()
            return log("Repeat selection")
        }

        io {
            try {
                val raw = file.readText()
                ui {
                    // Update markdown text
                    fileRaw.value = FileRaw(file, raw)

                    getPage(file)
                        ?.let { existingPage ->
                            // Use existing file
                            showingPage.value = existingPage

                            val index = pages.indexOf(existingPage)
                            pages[index] = pages[index]

                            io {
                                DaoManager.getPageDao(repo).apply {
                                    deleteByPath(existingPage.path)
                                    insertAll(existingPage)
                                }
                            }
                        }
                        ?: apply {
                            // Add new file
                            val newPage = Page(file.absolutePath)
                            pages.add(newPage)
                            showingPage.value = newPage

                            io {
                                DaoManager.getPageDao(repo).apply {
                                    deleteByPath(newPage.path)
                                    insertAll(newPage)
                                }
                            }
                        }

                    // Close and refresh drawer
                    showDrawer.value = Event(false)
                    updateDrawer()
                }
            } catch (e: Exception) {
                toast("Read file failed", e)
            }
        }

    }

    private fun getPage(file: File): Page? {
        var page: Page? = null
        pages.forEach {
            if (it.file == file) {
                page = it
            }
        }
        return page
    }

    fun removeShowingPage(): Boolean = isPositionValid() && pages.remove(showingPage.value)

    private fun isPositionValid() = currentTabPosition != -1 && currentTabPosition < pages.size

}