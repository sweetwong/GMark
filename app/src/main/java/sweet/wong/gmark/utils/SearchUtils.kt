package sweet.wong.gmark.utils

import android.content.Context
import android.net.ConnectivityManager
import sweet.wong.gmark.core.App


object SearchUtils {

    private fun networkConnectivity(): Boolean {
        val cm = App.app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun getGoogleSearchUrl(keyword: String): String {
        return "https://www.google.com/search?q=$keyword"
    }

    fun getBingSearchUrl(keyword: String): String {
        return "https://www.bing.com/search?q=$keyword"
    }
}