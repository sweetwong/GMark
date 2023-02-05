package sweet.wong.gmark.utils

import android.content.res.Configuration
import com.blankj.utilcode.util.PathUtils
import sweet.wong.gmark.core.App

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

    fun isDarkMode(): Boolean {
        return when (App.app.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

}