package sweet.wong.gmark.newrepo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import sweet.wong.gmark.R
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.databinding.ActivityNewRepoBinding
import sweet.wong.gmark.utils.EventObserver
import sweet.wong.gmark.utils.Utils

class NewRepoActivity : BaseActivity<ActivityNewRepoBinding>() {

    private val viewModel: NewRepoViewModel by viewModels()

    /**
     * Instead of [onActivityResult]
     */
    private val fileChooseLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri == null) return@registerForActivityResult

            binding.editSsh.setText(uri.path ?: return@registerForActivityResult)
        }

    private val requestPermissionThenChooseFileLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantState ->
            grantState.values.forEach { isGranted ->
                if (!isGranted) {
                    toast("Don't have permissions to access external files")
                    return@registerForActivityResult
                }
            }
            fileChooseLauncher.launch("file/*")
        }

    private val requestPermissionThenStartCloneLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantState ->
            grantState.values.forEach { isGranted ->
                if (!isGranted) {
                    toast("Don't have permissions to access external files")
                    return@registerForActivityResult
                }
            }
            createRepo()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()
    }

    private fun initView() = with(binding) {
        // Radio group to switch git http clone or git ssh clone
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_https -> {
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

        // Default is https
        radioGroup.check(R.id.radio_https)

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

    private fun createRepo() {
        val url = binding.inputUrl.editText?.text?.toString()
        if (url.isNullOrBlank()) {
            return toast("Url should not be empty")
        }

        if (binding.radioHttps.isChecked) {
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
        ).observe(this, EventObserver {
            setResult(RESULT_OK)
            finish()
        })
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
        ).observe(this, EventObserver {
            setResult(RESULT_OK)
            finish()
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        fun start(context: Context, addRepoLauncher: ActivityResultLauncher<Intent>) {
            addRepoLauncher.launch(Intent(context, NewRepoActivity::class.java))
        }

    }

}