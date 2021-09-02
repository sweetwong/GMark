package sweet.wong.sweetnote.repolist

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.data.Repo


/**
 * TODO: Add Description
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

    private val fileChooseLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            editSsh.setText(it.toString())
        }

    private lateinit var viewModel: RepoListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_MaterialComponents_Light_Dialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[RepoListViewModel::class.java]

        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // 找到视图
        radioGroup = view.findViewById(R.id.radio_group)
        inputUrl = view.findViewById(R.id.input_url)
        inputUsername = view.findViewById(R.id.input_username)
        inputPassword = view.findViewById(R.id.input_password)
        inputSsh = view.findViewById(R.id.input_ssh)
        editSsh = view.findViewById(R.id.edit_ssh)
        btnClone = view.findViewById(R.id.btn_clone)

        // 切换 HTTP 和 SSH
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
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

        // 默认选中 HTTP
        radioGroup.check(R.id.radio_http)

        inputSsh.setEndIconOnClickListener {
            fileChooseLauncher.launch("file/*")
        }

        btnClone.setOnClickListener {
            val repos = viewModel.repos.value
            repos.add(RepoUIState(NonNullLiveData(Repo(++count, "", "", "不错哦", "", "", ""))))
            viewModel.repos.value = repos
        }

    }

}

var count: Int = 0