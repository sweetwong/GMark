package sweet.wong.gmark.utils

import com.blankj.utilcode.util.TimeUtils

object TimeUtils {

    // TODO: 2021/9/20 这里应该更灵活点
    fun getRelativeString(last: Long = System.currentTimeMillis()): String {
        val now = System.currentTimeMillis()
        val diff = now - last
        return when {
            // Show minutes
            diff < 60 * 60 * 1000L -> {
                val res = diff / (60 * 1000L)
                "Updated $res ${if (res <= 1) "minute" else "minutes"} ago"
            }
            // Show hours
            diff < 24 * 60 * 60 * 1000L -> {
                val res = diff / (60 * 60 * 1000L)
                "Updated $res ${if (res <= 1) "hour" else "hours"} ago"
            }
            // Show days
            diff < 7 * 24 * 60 * 60 * 1000L -> {
                val res = diff / (24 * 60 * 60 * 1000L)
                "Updated $res ${if (res <= 1) "day" else "days"} ago"
            }
            // Show weeks
            diff < 4 * 7 * 24 * 60 * 60 * 1000L -> {
                val res = diff / (7 * 24 * 60 * 60 * 1000L)
                "Updated $res ${if (res <= 1) "week" else "weeks"} ago"
            }
            // Show full time
            else -> {
                "Updated in ${TimeUtils.millis2String(last, "yyyy-MM-dd")}"
            }
        }
    }

}