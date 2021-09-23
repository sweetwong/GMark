package sweet.wong.gmark.repo.drawer

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import sweet.wong.gmark.R
import sweet.wong.gmark.core.delay
import sweet.wong.gmark.databinding.LayoutDrawerBinding
import sweet.wong.gmark.repo.RepoActivity
import sweet.wong.gmark.repo.RepoViewModel
import sweet.wong.gmark.repo.git.GitFragment
import sweet.wong.gmark.repo.history.HistoryFragment
import sweet.wong.gmark.repo.outline.OutlineFragment
import sweet.wong.gmark.repo.project.ProjectFragment
import sweet.wong.gmark.settings.SettingsActivity
import sweet.wong.gmark.utils.Event

class DrawerDelegate(
    private val activity: RepoActivity,
    private val viewModel: RepoViewModel,
    private val binding: LayoutDrawerBinding
) {

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            activity.supportFragmentManager.commit {
                add<ProjectFragment>(R.id.fragment_container_view)
            }
        }
        binding.btnProject.isSelected = true

        binding.btnProject.setOnClickListener(::onClickDrawerButton)
        binding.btnOutline.setOnClickListener(::onClickDrawerButton)
        binding.btnHistory.setOnClickListener(::onClickDrawerButton)
        binding.btnGit.setOnClickListener(::onClickDrawerButton)
        binding.btnSettings.setOnClickListener(::onClickDrawerButton)
    }

    private fun onClickDrawerButton(button: View) {
        if (button !is ImageButton) {
            return
        }

        unSelectAll()
        button.isSelected = true

        when (button) {
            binding.btnProject -> {
                binding.drawerToolbar.title = activity.getString(R.string.project)
                activity.supportFragmentManager.commit {
                    replace<ProjectFragment>(R.id.fragment_container_view)
                }
            }
            binding.btnOutline -> {
                binding.drawerToolbar.title = activity.getString(R.string.outline)
                activity.supportFragmentManager.commit {
                    replace<OutlineFragment>(R.id.fragment_container_view)
                }
            }
            binding.btnGit -> {
                binding.drawerToolbar.title = activity.getString(R.string.git)
                activity.supportFragmentManager.commit {
                    replace<GitFragment>(R.id.fragment_container_view)
                }
            }
            binding.btnHistory -> {
                binding.drawerToolbar.title = activity.getString(R.string.history)
                activity.supportFragmentManager.commit {
                    replace<HistoryFragment>(R.id.fragment_container_view)
                }
            }
            binding.btnSettings -> {
                SettingsActivity.start(activity)
                delay(200) {
                    viewModel.showDrawer.value = Event(false)
                }
            }
        }
    }

    private fun unSelectAll() {
        binding.btnProject.isSelected = false
        binding.btnOutline.isSelected = false
        binding.btnHistory.isSelected = false
        binding.btnGit.isSelected = false
    }

}