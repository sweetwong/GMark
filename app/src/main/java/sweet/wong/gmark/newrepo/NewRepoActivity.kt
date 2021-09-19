package sweet.wong.gmark.newrepo

import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.databinding.ActivityNewRepoBinding
import sweet.wong.gmark.ext.start

class NewRepoActivity : BaseActivity<ActivityNewRepoBinding>() {

    private val viewModel: NewRepoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {

        fun start(context: Context) {
            context.start<NewRepoActivity>()
        }

    }

}