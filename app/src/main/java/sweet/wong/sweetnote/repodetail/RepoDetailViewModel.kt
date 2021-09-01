package sweet.wong.sweetnote.repodetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import java.io.File

class RepoDetailViewModel : ViewModel() {

    /**
     * 核心变量，代表着当前页面打开的文件绝对路径
     */
    val path = MutableLiveData<String>()

    /**
     * 当前抽屉页显示的文件夹
     */
    val drawerFold = path.map {
        File(it).parent ?: throw Exception("parent folder is null")
    } as MutableLiveData<String>

}