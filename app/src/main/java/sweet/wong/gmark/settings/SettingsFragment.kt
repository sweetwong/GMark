package sweet.wong.gmark.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import sweet.wong.gmark.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)
    }
}