package sweet.wong.gmark.repo.drawer.git

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.jgit.api.Git
import sweet.wong.gmark.ext.IO_CATCH
import java.io.File

class GitViewModel : ViewModel() {

    val diffUIStates = MutableLiveData<List<DiffUIState>>()

    fun refreshDiffList(file: File) = viewModelScope.launch(Dispatchers.IO_CATCH) {
        val uiStates = mutableListOf<DiffUIState>()
        Git.open(file).diff().call().forEach {
            uiStates.add(DiffUIState(it))
        }
        diffUIStates.postValue(uiStates)
    }

    fun commit() {

    }

}