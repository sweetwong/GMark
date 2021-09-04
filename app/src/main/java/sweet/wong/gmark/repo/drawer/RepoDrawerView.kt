package sweet.wong.gmark.repo.drawer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import sweet.wong.gmark.databinding.LayoutDrawerBinding
import sweet.wong.gmark.repo.RepoViewModel

class RepoDrawerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutDrawerBinding.inflate(LayoutInflater.from(context), this, true)

    private val activity: AppCompatActivity = context as AppCompatActivity
    private val viewModel: RepoViewModel by activity.viewModels()
    private val adapter: RepoDrawerProjectAdapter = RepoDrawerProjectAdapter(viewModel)

    init {
        binding.viewModel = viewModel
        binding.lifecycleOwner = activity

        binding.recyclerView.adapter = adapter

        viewModel.projectChildFiles.observe(activity) {
            adapter.submitList(it.toMutableList())
        }
    }

    fun onBackPressed() = adapter.onBackPressed()

}