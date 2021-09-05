package sweet.wong.gmark.repo.drawer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import sweet.wong.gmark.core.EventObserver
import sweet.wong.gmark.databinding.LayoutDrawerBinding
import sweet.wong.gmark.repo.RepoViewModel

class DrawerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutDrawerBinding.inflate(LayoutInflater.from(context), this, true)

    private val activity: AppCompatActivity = context as AppCompatActivity
    private val viewModel: RepoViewModel by activity.viewModels()
    private val projectAdapter: ProjectAdapter = ProjectAdapter(viewModel)

    init {
        binding.viewModel = viewModel
        binding.lifecycleOwner = activity

        binding.recyclerView.adapter = projectAdapter

        binding.projectText.setOnClickListener {
            viewModel.currentFile?.parentFile?.let {
                viewModel.currentProjectFolder.value = it
            }
        }

        binding.outlineText.setOnClickListener {
        }

        binding.historyText.setOnClickListener {
        }

        binding.navigationBar.onItemClick = {
            viewModel.currentProjectFolder.value = it
        }

        viewModel.projectChildFiles.observe(activity) {
            projectAdapter.submitList(it.toList())
            binding.navigationBar.updateFile(viewModel.currentProjectFolder.value!!, viewModel.rootFile)
        }

        viewModel.selectFileEvent.observe(activity, EventObserver {
            binding.navigationBar.updateFile(it, viewModel.rootFile)
        })
    }

    fun onBackPressed() = projectAdapter.onBackPressed()

    fun refreshProject() {
        projectAdapter.notifyDataSetChanged()
    }

}