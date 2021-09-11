package sweet.wong.gmark.repo.drawer

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import sweet.wong.gmark.R
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.LayoutDrawerBinding
import sweet.wong.gmark.repo.RepoActivity
import sweet.wong.gmark.repo.drawer.history.HistoryFragment
import sweet.wong.gmark.repo.drawer.project.ProjectFragment
import sweet.wong.gmark.repo.viewmodel.RepoViewModel
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

        binding.drawerToolbar.setNavigationOnClickListener {
            viewModel.drawerShowEvent.value = Event(false)
        }
    }

    private fun onClickDrawerButton(button: View) {
        if (button !is ImageButton) {
            return
        }

        unSelectAll()
        button.isSelected = true

        when (button) {
            binding.btnProject -> {
                activity.supportFragmentManager.commit {
                    replace<ProjectFragment>(R.id.fragment_container_view)
                }
            }
            binding.btnGit -> {
                toast("Click git")
            }
            binding.btnHistory -> {
                activity.supportFragmentManager.commit {
                    replace<HistoryFragment>(R.id.fragment_container_view)
                }
            }
            binding.btnOutline -> {
                toast("Click outline")
            }
            binding.btnSettings -> {
                SettingsActivity.start(activity)
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