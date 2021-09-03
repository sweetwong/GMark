package sweet.wong.sweetnote.filepreview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.utils.LimitedDeque
import java.io.File

class FilePreviewViewModel : ViewModel() {

    val repo = MutableLiveData<Repo>()

    /**
     * Point to current selected File, must be a file not a directory
     */
    val currentFile = MutableLiveData<File>()

    val fileText = MutableLiveData<String>()

    /**
     * Point to current drawer project folder, must be directory not a file
     */
    val currentProjectFolder = MutableLiveData<File>()

    val projectChildFiles = currentProjectFolder.switchMap {
        MutableLiveData(getFileList(it))
    }

    val historyStack = LimitedDeque<String>(5)

    fun selectFile(file: File) {
        Observable.fromCallable { file.readText() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                fileText.value?.takeIf { value -> value.isNotBlank() }?.let { value -> historyStack.add(value) }
                fileText.value = it
            }
            .doOnError {
                toast("Read text failed", it)
            }
            .subscribe()
    }

    fun init(repo: Repo) {
        this.repo.value = repo

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

        return childFiles
    }

}