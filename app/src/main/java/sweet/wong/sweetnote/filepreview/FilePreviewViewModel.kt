package sweet.wong.sweetnote.filepreview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import sweet.wong.sweetnote.core.Event
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.filepreview.history.HistoryFile
import sweet.wong.sweetnote.utils.LimitedDeque
import java.io.File

class FilePreviewViewModel : ViewModel() {

    /**
     * File raw text, this data may be large
     */
    val raw = MutableLiveData<String>()

    /**
     * Current drawer project folder, must be directory not a file
     */
    val currentProjectFolder = MutableLiveData<File>()

    val projectChildFiles = currentProjectFolder.switchMap {
        MutableLiveData(getFileList(it))
    }

    /**
     * Current Repository, this data is get from argument
     */
    var repo: Repo? = null

    /**
     * Current selected File, must be a file not a directory
     */
    var currentFile: File? = null

    var scrollY: Int = 0

    val historyStack = LimitedDeque<HistoryFile>(5)

    val selectFileEvent = MutableLiveData<Event<Unit>>()

    fun selectFile(file: File) {
        if (!file.exists() || !file.isFile) {
            return toast("File $file is not exist")
        }

        Observable.fromCallable { file.readText() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                val oldData = raw.value
                val oldFile = currentFile
                if (!oldData.isNullOrBlank() && oldFile != null) {
                    historyStack.add(HistoryFile(oldFile, oldData, scrollY))
                }

                raw.value = it
                currentFile = file
                selectFileEvent.value = Event(Unit)
            }
            .doOnError {
                toast("Read text failed", it)
            }
            .subscribe()
    }

    fun init(repo: Repo) {
        this.repo = repo

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

    private fun getFileList(folderFile: File): List<File> {
        if (!folderFile.exists()) return emptyList()
        if (!folderFile.isDirectory) return emptyList()

        val childFiles = mutableListOf<File>()
        folderFile.listFiles()?.forEach {
            childFiles.add(it)
        }

        return childFiles.reversed()
    }

}