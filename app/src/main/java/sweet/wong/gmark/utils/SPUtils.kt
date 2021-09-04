package sweet.wong.gmark.utils

import android.content.Context
import android.content.SharedPreferences
import sweet.wong.gmark.core.App

/**
 * [SharedPreferences] 工具类
 *
 * @author sweetwang 2021/7/21
 */
object SPUtils {

    private const val SP_NAME = "android_uikit"

    private val sp: SharedPreferences
        get() = App.app.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor
        get() = sp.edit()

    fun putInt(key: String, value: Int, commit: Boolean = true) = with(editor) {
        putInt(key, value)
        if (!commit || !commit()) apply()
    }

    fun getInt(key: String, defValue: Int = 0): Int = sp.getInt(key, defValue)

    fun putBoolean(key: String, value: Boolean, commit: Boolean = true) = with(editor) {
        putBoolean(key, value)
        if (!commit || !commit()) apply()
    }

    fun getBoolean(key: String, defValue: Boolean = false): Boolean = sp.getBoolean(key, defValue)

    fun putString(key: String, value: String, commit: Boolean = true) = with(editor) {
        putString(key, value)
        if (!commit || !commit()) apply()
    }

    fun getString(key: String, defValue: String? = null): String? = sp.getString(key, defValue)

}