package sweet.wong.gmark.repo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.DaoManager
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.data.PageType
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.ext.MAIN_CATCH
import sweet.wong.gmark.ext.notify
import sweet.wong.gmark.repo.drawer.project.ProjectUIState
import sweet.wong.gmark.sp.SPConstant
import sweet.wong.gmark.sp.SPUtils
import sweet.wong.gmark.utils.Event
import sweet.wong.gmark.utils.NonNullLiveData
import java.io.File

class RepoViewModel : ViewModel() {

    /**
     * This is used to control drawer
     */
    val showDrawer = MutableLiveData<Event<Boolean>>()

    /**
     * This is drawer callback
     */
    val onDrawerShow = MutableLiveData<Boolean>()

    val drawerFolder = MutableLiveData<ProjectUIState>()

    val pages = NonNullLiveData<MutableList<Page>>(mutableListOf())

    val webViewNameUpdateEvent = MutableLiveData<Event<String>>()

    var currentPosition = -1

    val showingPage: Page?
        get() = pages.value.getOrNull(currentPosition)

    val showingFile: File?
        get() = showingPage?.file

    val onTabReselect = MutableLiveData<Unit>()

    /**
     * Current Repository, this data is get from argument
     */
    lateinit var repo: Repo

    lateinit var rootFile: File

    var isRenaming: Boolean = false

    fun init(): Boolean {
        // 1: Get recent loaded url by shared preferences
        val url = SPUtils.getString(SPConstant.RECENT_REPO_URL) ?: return false
        // 2: Get repo model by room
        val repo = DaoManager.repoDao.get(url) ?: return false
        // 3: Ensure repo has downloaded successfully
        if (repo.state != Repo.STATE_SUCCESS) return false
        // 4: Ensure local directory exists
        rootFile = File(repo.root).apply { if (!isDirectory) return false }

        // Now we init success
        this.repo = repo
        return true
    }

    fun loadREADME() {
        val readMeFile = rootFile.listFiles()?.find { it.name == "README.md" }
        if (readMeFile != null) {
            selectFile(readMeFile)
        } else {
            drawerFolder.value = ProjectUIState(rootFile, rootFile, rootFile)
            showDrawer.value = Event(true)
        }
    }

    fun updateDrawer() {
        val showingFile = this.showingFile ?: return
        val drawerFile = showingFile.parentFile ?: return

        drawerFolder.value = ProjectUIState(drawerFile, showingFile, rootFile)
    }

    fun renameFile(oldFile: File, newFile: File) = viewModelScope.launch(Dispatchers.MAIN_CATCH) {
        isRenaming = true
        val pages = pages.value
        val oldPage = pages.find { it.file == oldFile }
        if (oldPage != null) {
            withContext(Dispatchers.IO) {
                DaoManager.getPageDao(repo).delete(oldPage)
            }
            pages.remove(oldPage)
        }
        selectFile(newFile)
        isRenaming = false
    }

    fun selectPage(page: Page?) {
        if (page?.pageType == PageType.FILE) {
            selectFile(page.file)
        }

        if (page?.pageType == PageType.URL) {
            selectUrl(page.path)
        }
    }

    fun selectUrl(url: String) {
        val urlPage = Page(path = url)
        pages.value.add(++currentPosition, urlPage)
        pages.notify()
    }

    /**
     * There are three cases:
     *
     * 1. Choose same file
     * 2. Choose existing file
     * 3. Choose new file
     */
    fun selectFile(file: File?, forceUpdate: Boolean = false) = viewModelScope.launch {
        // Check file valid
        if (file == null || !file.exists() || !file.isFile) {
            toast("File $file is not exist")
            return@launch
        }

        // Check Same file
        if (file == showingFile && !forceUpdate) {
            // Close and refresh drawer
            showDrawer.value = Event(false)
            updateDrawer()
            log("Repeat selection")
            return@launch
        }

        try {
            val pages = pages.value

            pages.find { it.file == file }
                ?.let { existingPage ->
                    val position = pages.indexOf(existingPage)
                    currentPosition = position
                    this@RepoViewModel.pages.notify()

                    withContext(Dispatchers.IO) {
                        DaoManager.getPageDao(repo).apply {
                            deleteByPath(existingPage.path)
                            insertAll(existingPage)
                        }
                    }
                }
                ?: apply {
                    // Add new file
                    val newPage = Page(file.absolutePath)
                    pages.add(++currentPosition, newPage)
                    this@RepoViewModel.pages.notify()

                    withContext(Dispatchers.IO) {
                        DaoManager.getPageDao(repo).apply {
                            deleteByPath(newPage.path)
                            insertAll(newPage)
                        }
                    }
                }

            // Close and refresh drawer
            showDrawer.value = Event(false)
            updateDrawer()
        } catch (e: Exception) {
            e.printStackTrace()
            toast("Read file failed", e)
        }
    }

    fun pageSize(): Int = pages.value.size

    fun removeShowingPage(): Boolean {
        val pages = pages.value
        val showingPage = showingPage ?: return false
        val success = pages.remove(showingPage)
        if (success && currentPosition > 0) {
            currentPosition--
        }
        this.pages.notify()
        return success
    }

}