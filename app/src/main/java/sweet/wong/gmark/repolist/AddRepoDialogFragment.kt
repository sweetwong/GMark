package sweet.wong.gmark.repolist

import android.Manifest
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import sweet.wong.gmark.R
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.databinding.DialogAddRepoBinding
import sweet.wong.gmark.utils.Utils

/**
 * Dialog Fragment which is used to input necessary information for git clone
 */
class RepoAuthDialogFragment(private val viewModel: RepoListViewModel) :
    DialogFragment(R.layout.dialog_add_repo) {

    private lateinit var binding: DialogAddRepoBinding

    /**
     * Instead of [onActivityResult]
     */
    private val fileChooseLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
        if (uri == null) return@registerForActivityResult

        binding.editSsh.setText(uri.path ?: return@registerForActivityResult)
    }

    private val requestPermissionThenChooseFileLauncher =
        registerForActivityResult(RequestMultiplePermissions()) { grantState ->
            grantState.values.forEach { isGranted ->
                if (!isGranted) {
                    toast("Don't have permissions to access external files")
                    return@registerForActivityResult
                }
            }
            fileChooseLauncher.launch("file/*")
        }

    private val requestPermissionThenStartCloneLauncher =
        registerForActivityResult(RequestMultiplePermissions()) { grantState ->
            grantState.values.forEach { isGranted ->
                if (!isGranted) {
                    toast("Don't have permissions to access external files")
                    dismiss()
                    return@registerForActivityResult
                }
            }
            createRepo()
            dismiss()
        }

    /**
     * Interface to listen dialog dismiss
     */
    var onDismiss: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set style method should be called before onCreateView
        setStyle(STYLE_NO_TITLE, R.style.Theme_MaterialComponents_Light_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAddRepoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // Radio group to switch git http clone or git ssh clone
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_http -> {
                    inputUsername.isVisible = true
                    inputPassword.isVisible = true
                    inputSsh.isVisible = false
                    inputPassphrase.isVisible = false
                }
                R.id.radio_ssh -> {
                    inputUsername.isVisible = false
                    inputPassword.isVisible = false
                    inputSsh.isVisible = true
                    inputPassphrase.isVisible = true
                }
            }
        }

        // Default is hhtp
        radioGroup.check(R.id.radio_http)

        // Choose ssh private key
        inputSsh.setEndIconOnClickListener {
            requestPermissionThenChooseFileLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

        // Start clone
        btnClone.setOnClickListener {
            requestPermissionThenStartCloneLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.let { it() }
    }

    private fun createRepo() {
        val url = binding.inputUrl.editText?.text?.toString()
        if (url.isNullOrBlank()) {
            return toast("Url should not be empty")
        }

        if (binding.radioHttp.isChecked) {
            createHttpRepo(url)
        } else {
            createSshRepo(url)
        }
    }

    private fun createHttpRepo(url: String) {
        val username = binding.inputUsername.editText?.text?.toString()
        val password = binding.inputPassword.editText?.text?.toString()

        viewModel.addNewRepo(
            Repo(
                url,
                Utils.getRepoPath(url),
                Utils.getTitleByGitUrl(url),
                username,
                password,
                null
            )
        )
    }

    private fun createSshRepo(url: String) {
        val sshKey = binding.inputSsh.editText?.text?.toString()
        if (sshKey.isNullOrBlank()) {
            return toast("Please select ssh private key")
        }

        // TODO: 2021/9/3 Support ssh passphrase
        viewModel.addNewRepo(
            Repo(
                url,
                Utils.getRepoPath(url),
                Utils.getTitleByGitUrl(url),
                null,
                null,
                Utils.replaceExternalFiles(sshKey)
            )
        )
    }

}