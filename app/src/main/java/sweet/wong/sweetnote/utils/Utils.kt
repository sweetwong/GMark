package sweet.wong.sweetnote.utils

import com.blankj.utilcode.util.PathUtils

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/2
 */
object Utils {

    fun getRepoPath(url: String): String {
        return PathUtils.getExternalAppFilesPath() + "/" + getTitleByGitUrl(url)
    }

    fun getTitleByGitUrl(url: String): String {
        return StringBuilder().apply {
            url.replace(".git", "").reversed().forEach {
                if (it == '/') {
                    return reverse().toString()
                }
                append(it)
            }
        }.toString()
    }

    fun replaceExternalFiles(source: String): String {
        return source.replace("/external_files", PathUtils.getExternalStoragePath())
    }


}