package sweet.wong.gmark.utils

import sweet.wong.gmark.R
import sweet.wong.gmark.core.getString
import sweet.wong.gmark.sp.SPUtils


object SearchUtils {

    fun getSearchUrlByPref(keyword: String): String {
        val searchEngineValue = SPUtils.settings.getString(
            getString(R.string.pref_search_engine), SearchEngine.GOOGLE.value
        )
        return when (SearchEngine.valueOf(searchEngineValue)) {
            SearchEngine.GOOGLE -> "https://www.google.com/search?q=$keyword"
            SearchEngine.BING -> "https://www.bing.com/search?q=$keyword"
            SearchEngine.BAIDU -> "https://www.baidu.com/s?wd=$keyword"
        }
    }
}

enum class SearchEngine(val value: String) {

    GOOGLE("google"),
    BING("bing"),
    BAIDU("baidu");

    companion object {

        fun valueOf(value: String?) = values().find { it.value == value } ?: GOOGLE

    }

}