package sweet.wong.gmark.settings

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.GsonUtils
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
            val email = "yuanlai5chaos@gmail.com"
            val provider = OAuthProvider.newBuilder("github.com")
                .addCustomParameter("login", email)
                .setScopes(listOf("repo"))

            Firebase.auth
                .startActivityForSignInWithProvider(requireActivity(), provider.build())
                .addOnSuccessListener { authResult ->
                    val userInfo = authResult.additionalUserInfo
                        ?: return@addOnSuccessListener
                    val profile = StringBuilder().apply {
                        userInfo.profile?.forEach {
                            append("${it.key}: ${it.value}\n")
                        }
                    }

                    val token = (authResult.credential as? OAuthCredential)?.accessToken
                        ?: return@addOnSuccessListener

                    val avatar = authResult.additionalUserInfo?.profile?.get("avatar_url")
                            as? String

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
                }
                .addOnFailureListener {
                    toast("失败", it)
                }


            true
        }
    }

}