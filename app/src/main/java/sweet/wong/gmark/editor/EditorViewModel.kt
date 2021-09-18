package sweet.wong.gmark.editor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import java.io.File

class EditorViewModel : ViewModel() {

    val file = MutableLiveData<File>().apply {
        observeForever { file ->
            viewModelScope.launch {
                try {
                    toolbarTitle.value = file.name
                    val rawText = withContext(Dispatchers.IO) {
                        file.readText()
                    }
                    editingText.value = rawText
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("Read file failed", e)
                }
            }
        }
    }

    val toolbarTitle = MutableLiveData<String>()

    val editingText = MutableLiveData<String>().apply {
        observeForever {
            log("你好呀", it)
        }
    }

}