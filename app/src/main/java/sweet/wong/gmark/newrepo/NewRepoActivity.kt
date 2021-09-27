package sweet.wong.gmark.newrepo

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import sweet.wong.gmark.R
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.databinding.ActivityNewRepoBinding
import sweet.wong.gmark.utils.SinglePost
import sweet.wong.gmark.utils.Utils

class NewRepoActivity : BaseActivity<ActivityNewRepoBinding>() {

    private val viewModel: NewRepoViewModel by viewModels()
    private var urlFirstEdit = true

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
        val singlePost = SinglePost()
        inputUrl.editText?.doOnTextChanged { text, _, _, _ ->
            val url = text?.toString().orEmpty()
            if (urlFirstEdit) {
                refreshInputLayouts(url)
                urlFirstEdit = false
            } else {
                singlePost.postDelayed(300) {
                    refreshInputLayouts(url)
                }
            }
        }
        inputUrl.editText?.setText(R.string.gmark_url)

        // Choose ssh private key
        inputSsh.setEndIconOnClickListener {
            requestPermissionThenChooseFileLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun refreshInputLayouts(url: String) = with(binding) {
        when (CloneUrlParser.parse(url)) {
            CloneUrlType.HTTP -> {
                changeInputsVisible(username = true, password = true)
            }
            CloneUrlType.SSH -> {
                changeInputsVisible(ssh = true, passphrase = true)
            }
            CloneUrlType.GITHUB -> {
                changeInputsVisible(github = true)
            }
            CloneUrlType.INVALID -> {
                changeInputsVisible()
            }
        }
    }

    private fun changeInputsVisible(
        username: Boolean = false,
        password: Boolean = false,
        ssh: Boolean = false,
        passphrase: Boolean = false,
        github: Boolean = false
    ) = with(binding) {
        inputUsername.isVisible = username
        inputPassword.isVisible = password
        inputSsh.isVisible = ssh
        inputPassphrase.isVisible = passphrase
        inputGithub.isVisible = github
    }

    // TODO: 2021/9/25 Migrate to view model
    private fun createRepo() {
        val url = binding.inputUrl.editText?.text?.toString()
        if (url.isNullOrBlank()) {
            toast("Url should not be empty")
            return
        }

        if (!url.endsWith(".git")) {
            toast("Url should end with .git")
            return
        }

        when (CloneUrlParser.parse(url)) {
            CloneUrlType.HTTP -> createHttpRepo(url)
            CloneUrlType.SSH -> createSshRepo(url)
            CloneUrlType.GITHUB -> createGithubRepo(url)
            CloneUrlType.INVALID -> toast("Url is invalid")
        }
    }

    private fun createGithubRepo(url: String) {
        viewModel.addNewRepo(
            Repo(
                url,
                Utils.getRepoPath(url),
                Utils.getTitleByGitUrl(url),
                "",
                "",
                null
            )
        ).observe(this) {
            setResult(RESULT_OK)
            finish()
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
        ).observe(this) {
            setResult(RESULT_OK)
            finish()
        }
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
        ).observe(this) {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_new_repo, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_clone -> {
                requestPermissionThenStartCloneLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun start(context: Context, addRepoLauncher: ActivityResultLauncher<Intent>) {
            addRepoLauncher.launch(Intent(context, NewRepoActivity::class.java))
        }

    }

}