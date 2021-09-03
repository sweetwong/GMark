package sweet.wong.sweetnote.repolist

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.databinding.DialogAddRepoBinding
import sweet.wong.sweetnote.utils.Utils

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
                }
                R.id.radio_ssh -> {
                    inputUsername.isVisible = false
                    inputPassword.isVisible = false
                    inputSsh.isVisible = true
                }
            }
        }

        // Default is hhtp
        radioGroup.check(R.id.radio_http)

        // When click ssh edit text end icon, we should open file chooser and select ssh private key
        inputSsh.setEndIconOnClickListener {
            fileChooseLauncher.launch("file/*")
        }

        // When click clone button, modify view model's data then trigger git clone action and last save to local storage
        btnClone.setOnClickListener {
            createRepo()
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

    }

    private fun createSshRepo(url: String) {
        val sshKey = binding.inputSsh.editText?.text?.toString()
        if (sshKey.isNullOrBlank()) {
            return toast("Please select ssh private key")
        }

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