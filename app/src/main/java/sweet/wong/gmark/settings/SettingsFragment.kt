package sweet.wong.gmark.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blankj.utilcode.util.AppUtils
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import sweet.wong.gmark.R
import sweet.wong.gmark.core.toast

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

        val prefGithub = findPreference<Preference>(getString(R.string.pref_github_auth))
        prefGithub?.setOnPreferenceClickListener {
            val provider = OAuthProvider.newBuilder("github.com")
                .addCustomParameter("login", "yuanlai5chaos@gmail.com")
                .setScopes(listOf("user:email"))

            Firebase.auth
                .startActivityForSignInWithProvider(requireActivity(), provider.build())
                .addOnSuccessListener {
                    toast("成功", it)
                }
                .addOnFailureListener {
                    toast("失败", it)
                }


            true
        }
    }

}