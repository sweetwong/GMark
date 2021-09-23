package sweet.wong.gmark.repo.git

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import sweet.wong.gmark.core.log
import java.io.File

class GitViewModel : ViewModel() {

    val diffEntries = MutableLiveData<List<DiffEntry>>()

    fun refresh(file: File) {
        val git = Git.open(file)
        val diffEntries = git.diff().call()
        this.diffEntries.value = diffEntries

        diffEntries.forEach {
            log(it)
        }
    }

}