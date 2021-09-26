package sweet.wong.gmark.repo.git

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.jgit.api.Git
import sweet.wong.gmark.ext.IO_CATCH
import java.io.File

class GitViewModel : ViewModel() {

    val uiStates = MutableLiveData<List<DiffUIState>>()

    fun refresh(file: File) = viewModelScope.launch(Dispatchers.IO_CATCH) {
        val uiStates = mutableListOf<DiffUIState>()
        Git.open(file).diff().call().forEach {
            uiStates.add(DiffUIState(it))
        }
        this@GitViewModel.uiStates.postValue(uiStates)
    }

}