package sweet.wong.gmark.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.blankj.utilcode.util.AppUtils
import sweet.wong.gmark.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)

        val prefTextSize = findPreference<EditTextPreference>(getString(R.string.pref_text_size))
        prefTextSize?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val prefColorTheme = findPreference<ListPreference>(getString(R.string.pref_theme_color))
        prefColorTheme?.setOnPreferenceChangeListener { _, _ ->
            AppUtils.relaunchApp()
            true
        }
    }

}