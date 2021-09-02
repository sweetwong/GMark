package sweet.wong.sweetnote.repolist

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.databinding.ActivityRepoListBinding
import java.util.*

/**
 * 仓库选择页面
 *
 * @author sweetwang 2021/9/1
 */
class RepoListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRepoListBinding
    private lateinit var repoListAdapter: RepoListAdapter
    private val viewModel: RepoListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind view
        binding = ActivityRepoListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Init ActionBar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Repository List"

        // When click fab
        binding.fab.setOnClickListener {
            binding.fab.isEnabled = false
            val dialogFragment = RepoAuthDialogFragment(viewModel)
            dialogFragment.onDismiss = { binding.fab.isEnabled = true }
            dialogFragment.show(supportFragmentManager, RepoAuthDialogFragment::class.java.name)
        }

        // Init RecyclerView
        repoListAdapter = RepoListAdapter(viewModel)
        binding.repoList.adapter = repoListAdapter
        viewModel.repoUIStates.observe(this@RepoListActivity) {
            repoListAdapter.submitList(it.toMutableList())
        }

        // Refresh repo list
        viewModel.refreshRepoList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_repo_list, menu)
        return true
    }


}