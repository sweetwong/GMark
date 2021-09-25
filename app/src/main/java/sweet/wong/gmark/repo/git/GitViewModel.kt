package sweet.wong.gmark.repo.git

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class GitViewModel : ViewModel() {

    val uiState = MutableLiveData<DiffUIState>()

    fun refresh(file: File) {
//        val git = Git.open(file)
//        val diffEntries = git.diff().call()
//
//        diffEntries.forEach {
//            log(it)
//        }
    }

}