package sweet.wong.gmark.utils

import android.content.Context
import sweet.wong.gmark.R
import sweet.wong.gmark.core.log
import sweet.wong.gmark.sp.SPUtils

object ThemeUtils {

    fun setTheme(context: Context) {
        val color = SPUtils.settings.getString(context.getString(R.string.pref_theme_color), null)
        log("setTheme", color)
        when (color) {
            "blue" -> {
                context.setTheme(R.style.Theme_GMark_BLUE)
            }
            "red" -> {
                context.setTheme(R.style.Theme_GMark_RED)
            }
            "yellow" -> {
                context.setTheme(R.style.Theme_GMark_YELLOW)
            }
            else -> {

            }
        }
    }

}