package sweet.wong.gmark.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sweet.wong.gmark.ext.IO_CATCH
import sweet.wong.gmark.utils.Event
import sweet.wong.gmark.utils.NonNullLiveDataList
import java.io.File

class SearchViewModel : ViewModel() {

    val fileSearchResult = NonNullLiveDataList<FileSearchResult>()

    val keyword = MutableLiveData<String>()

    val selectFileEvent = MutableLiveData<Event<File>>()

    lateinit var root: File

    fun searchFileDelayed(keyword: String) = viewModelScope.launch(Dispatchers.IO_CATCH) {
        if (keyword.isBlank()) {
            fileSearchResult.postValue(emptyList())
            this@SearchViewModel.keyword.postValue("")
            return@launch
        }

        val result = mutableListOf<FileSearchResult>().apply {
            root.walk().forEach { file ->
                val relative = file.toRelativeString(root)
                if (!relative.startsWith(".git")
                    && relative.contains(keyword, true)
                    && file.isFile
                ) {
                    add(FileSearchResult(file, relative))
                }
            }
        }
        fileSearchResult.postValue(result)
        this@SearchViewModel.keyword.postValue(keyword)
    }

    fun init(root: String, path: String) {
        this.root = File(root)
    }

//    private fun isSubSequence(sequence: String, s: String): Boolean {
//        val n = sequence.length
//        val m = s.length
//        var i = 0
//        var j = 0
//        while (i < n && j < m) {
//            if (sequence[i] == s[j]) {
//                i++
//            }
//            j++
//        }
//        return i == n
//    }
}