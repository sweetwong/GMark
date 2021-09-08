package sweet.wong.gmark.repo

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.fragment.app.add
import androidx.fragment.app.commit
import sweet.wong.gmark.R
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.LayoutDrawerBinding
import sweet.wong.gmark.repo.project.ProjectFragment
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

        binding.btnProject.setOnClickListener(::onClickDrawerButton)
        binding.btnGit.setOnClickListener(::onClickDrawerButton)
        binding.btnHistory.setOnClickListener(::onClickDrawerButton)
        binding.btnHistory.setOnClickListener(::onClickDrawerButton)

        binding.drawerToolbar.setNavigationOnClickListener {
            viewModel.drawerShowEvent.value = Event(false)
        }
    }

    private fun onClickDrawerButton(button: View) {
        if (button !is ImageButton) {
            return
        }

        when (button) {
            binding.btnProject -> {
                toast("Click project")
            }
            binding.btnGit -> {
                toast("Click git")
            }
            binding.btnHistory -> {
                toast("Click history")
            }
            binding.btnOutline -> {
                toast("Click outline")
            }
        }
    }


}