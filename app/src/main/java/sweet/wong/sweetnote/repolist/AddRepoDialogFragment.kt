package sweet.wong.sweetnote.repolist

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.data.Repo

/**
 * Dialog Fragment which is used to input necessary information for git clone
 *
 * @author sweetwang 2021/9/2
 */
class RepoAuthDialogFragment : DialogFragment(R.layout.dialog_add_repo) {

    private lateinit var radioGroup: RadioGroup
    private lateinit var inputUrl: TextInputLayout
    private lateinit var inputUsername: TextInputLayout
    private lateinit var inputPassword: TextInputLayout
    private lateinit var inputSsh: TextInputLayout
    private lateinit var editSsh: TextInputEditText
    private lateinit var btnClone: Button

    /**
     * Instead of [onActivityResult]
     */
    private val fileChooseLauncher = registerForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let { editSsh.setText(it.toString()) }
    }

    /**
     * Share view model with host activity
     *
     * Note that it's lifecycle owner is activity, not fragment
     */
    private lateinit var viewModel: RepoListViewModel

    /**
     * Interface to listen dialog dismiss
     */
    var onDismiss: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set style method should be called before onCreateView
        setStyle(STYLE_NO_TITLE, R.style.Theme_MaterialComponents_Light_Dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Init view model
        viewModel = ViewModelProvider(requireActivity())[RepoListViewModel::class.java]

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // Find views
        radioGroup = view.findViewById(R.id.radio_group)
        inputUrl = view.findViewById(R.id.input_url)
        inputUsername = view.findViewById(R.id.input_username)
        inputPassword = view.findViewById(R.id.input_password)
        inputSsh = view.findViewById(R.id.input_ssh)
        editSsh = view.findViewById(R.id.edit_ssh)
        btnClone = view.findViewById(R.id.btn_clone)

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
            val repos = viewModel.repos.value
            repos.add(RepoUIState(NonNullLiveData(Repo(++count, "", "", "不错哦", "", "", ""))))
            viewModel.repos.value = repos
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismiss?.let { it() }
    }

}

var count: Int = 0