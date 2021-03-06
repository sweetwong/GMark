package sweet.wong.gmark.repolist

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.viewModels
import sweet.wong.gmark.R
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.ActivityRepoListBinding
import sweet.wong.gmark.ext.start
import sweet.wong.gmark.newrepo.NewRepoActivity
import sweet.wong.gmark.repo.RepoActivity
import sweet.wong.gmark.settings.SettingsActivity
import sweet.wong.gmark.sp.SPConstant
import sweet.wong.gmark.sp.SPUtils
import sweet.wong.gmark.utils.EventObserver
import java.util.*

/**
 * Repository list page
 */
class RepoListActivity : BaseActivity<ActivityRepoListBinding>() {

    private lateinit var repoListAdapter: RepoListAdapter
    private val viewModel: RepoListViewModel by viewModels()

    private val addRepoLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            viewModel.refreshRepoList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Init ActionBar
        setSupportActionBar(binding.toolbar)

        // When click fab
        binding.fab.setOnClickListener {
            NewRepoActivity.start(this, addRepoLauncher)
        }

        // Init RecyclerView
        repoListAdapter = RepoListAdapter(viewModel, this)
        binding.repoList.adapter = repoListAdapter
        viewModel.repoUIStates.observe(this@RepoListActivity) {
            repoListAdapter.submitList(it.toMutableList())
        }

        // Refresh repo list
        viewModel.refreshRepoList()

        viewModel.repoSelectEvent.observe(this, EventObserver { repo ->
            SPUtils.putString(SPConstant.RECENT_REPO_URL, repo.url, true)
            RepoActivity.start(this)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_repo_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sync -> {
                toast("Sync")
                return true
            }
            R.id.menu_settings -> {
                SettingsActivity.start(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun start(context: Context) = context.start<RepoListActivity>()
    }

}