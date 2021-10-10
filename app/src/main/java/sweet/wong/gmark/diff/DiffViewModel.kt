package sweet.wong.gmark.diff

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.treewalk.filter.PathFilter
import sweet.wong.gmark.ext.IO_CATCH
import java.io.ByteArrayOutputStream
import java.io.File

class DiffViewModel : ViewModel() {

    lateinit var path: String
    lateinit var root: String

    val text = MutableLiveData<String>()

    fun init() {
        viewModelScope.launch(Dispatchers.IO_CATCH) {
            val output = getDiffOutput()
            text.postValue(output)
        }
    }

    private fun getDiffOutput(): String {
        val output = ByteArrayOutputStream()
        Git.open(File(root)).diff()
            .setPathFilter(PathFilter.create(path))
            .setOutputStream(output)
            .call()
        return output.toString("UTF-8")
    }

}