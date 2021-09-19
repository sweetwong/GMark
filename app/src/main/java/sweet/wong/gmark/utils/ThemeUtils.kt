package sweet.wong.gmark.utils

import android.content.Context
import sweet.wong.gmark.R
import sweet.wong.gmark.sp.SPUtils

object ThemeUtils {

    private const val THEME_BLUE = "blue"
    private const val THEME_RED = "red"
    private const val THEME_YELLOW = "yellow"

    private val THEMES = mapOf(
        THEME_BLUE to R.style.Theme_GMark_BLUE,
        THEME_RED to R.style.Theme_GMark_RED,
        THEME_YELLOW to R.style.Theme_GMark_YELLOW
    )

    fun setTheme(context: Context) {
        val color =
            SPUtils.settings.getString(context.getString(R.string.pref_theme_color), THEME_BLUE)
        THEMES[color]?.let { context.setTheme(it) }
    }

}