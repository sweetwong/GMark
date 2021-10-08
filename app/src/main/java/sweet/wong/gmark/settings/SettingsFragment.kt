package sweet.wong.gmark.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.RegexUtils
import com.google.firebase.auth.OAuthCredential
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import sweet.wong.gmark.R
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.GithubAuthData
import sweet.wong.gmark.sp.SPConstant
import sweet.wong.gmark.sp.SPUtils
import java.util.*

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.pref_settings, rootKey)

        val prefLanguage = findPreference<ListPreference>(getString(R.string.pref_language))
        prefLanguage?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                "system_language" -> {
                    LanguageUtils.applySystemLanguage(false)
                }
                "english" -> {
                    LanguageUtils.applyLanguage(Locale.ENGLISH, false)
                }
                "chinese" -> {
                    LanguageUtils.applyLanguage(Locale.CHINESE, false)
                }
            }
            true
        }

        val prefTextSize = findPreference<EditTextPreference>(getString(R.string.pref_text_size))
        prefTextSize?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        val prefColorTheme = findPreference<ListPreference>(getString(R.string.pref_theme_color))
        prefColorTheme?.setOnPreferenceChangeListener { _, _ ->
            AppUtils.relaunchApp()
            true
        }

        val prefGithub = findPreference<EditTextPreference>(getString(R.string.pref_github_auth))
        prefGithub?.setOnPreferenceChangeListener { preference, email ->
            if (email !is String || !RegexUtils.isEmail(email)) {
                toast("Please input valid email text")
                return@setOnPreferenceChangeListener false
            }
            startGithubOAuth(email, preference as EditTextPreference)
            true
        }
    }

    private fun startGithubOAuth(email: String, preference: EditTextPreference) {
        val provider = OAuthProvider.newBuilder("github.com")
            .addCustomParameter("login", email)
            .setScopes(listOf("repo"))

        Firebase.auth
            .startActivityForSignInWithProvider(requireActivity(), provider.build())
            .addOnSuccessListener { authResult ->
                val userInfo = authResult.additionalUserInfo ?: return@addOnSuccessListener
                val profile = StringBuilder().apply {
                    userInfo.profile?.forEach {
                        append("${it.key}: ${it.value}\n")
                    }
                }

                val token = (authResult.credential as? OAuthCredential)?.accessToken
                    ?: return@addOnSuccessListener

                val avatar = authResult.additionalUserInfo?.profile?.get("avatar_url") as? String

                log(
                    """
                        isNewUser: ${userInfo.isNewUser}
                        profile: $profile
                        providerId: ${userInfo.providerId}
                        username: ${userInfo.username}
                        token: ${(authResult.credential as? OAuthCredential)?.accessToken}
                    """.trimIndent()
                )

                val data = GithubAuthData(email, token, avatar)
                SPUtils.putString(SPConstant.GITHUB_AUTH_DATA, GsonUtils.toJson(data))

                toast("Auth success")
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
                toast("失败", e)
                preference.text = "\"{email}\" auth failed"
            }
    }


}