package sweet.wong.gmark.repo.project

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputLayout
import sweet.wong.gmark.R
import sweet.wong.gmark.databinding.FragmentProjectBinding
import sweet.wong.gmark.repo.drawer.DrawerFragment

class ProjectFragment : DrawerFragment<FragmentProjectBinding>() {

    private val viewModel: ProjectViewModel by viewModels()

    private lateinit var browserAdapter: FileBrowserAdapter

    private lateinit var navBarAdapter: NavigationBarAdapter

    override val menuRes: Int
        get() = R.menu.menu_project

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.repoViewModel = repoViewModel

        // File browser Adapter
        browserAdapter = FileBrowserAdapter(viewModel, viewLifecycleOwner) { selected ->
            if (selected.isNavigateBack) {
                val parent = ProjectUIState(
                    selected, selected.drawerFile.parentFile ?: return@FileBrowserAdapter
                )
                viewModel.selectDrawerFile(parent)
                return@FileBrowserAdapter
            }
            if (selected.drawerFile.isDirectory) {
                viewModel.selectDrawerFile(selected)
                return@FileBrowserAdapter
            }
            if (selected.drawerFile.isFile) {
                repoViewModel.selectFile(selected.drawerFile)
            }
        }

        // Navigation bar adapter
        navBarAdapter = NavigationBarAdapter { selected ->
            viewModel.selectDrawerFile(selected)
        }

        // Binding adapter
        binding.fileBrowser.adapter = browserAdapter
        binding.fileBrowser.itemAnimator = null
        binding.navigationBar.adapter = navBarAdapter

        // Observe drawer change event
        repoViewModel.drawerFolder.observe(viewLifecycleOwner) {
            viewModel.selectDrawerFile(it)
        }

        // Observe file browser list refresh
        viewModel.fileBrowserList.observe(viewLifecycleOwner) {
            browserAdapter.submitList(it)
        }

        // Observe navigation bar list refresh
        viewModel.navBarList.observe(viewLifecycleOwner) {
            navBarAdapter.submitList(it) {
                binding.navigationBar.scrollToPosition(it.lastIndex)
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onMenuItemSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.menu_add -> {
                // Create EditText
                val inputFileName: TextInputLayout =
                    layoutInflater.inflate(R.layout.dialog_edit_text, null) as TextInputLayout

                // Show dialog
                AlertDialog.Builder(requireContext())
                    .setView(inputFileName)
                    .setNegativeButton(R.string.cancel) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(R.string.confirm) { dialog, which ->
                        val fileName = inputFileName.editText?.text?.toString().orEmpty()
                        viewModel.newFile(fileName)
                    }
                    .create()
                    .apply {
                        inputFileName.requestFocus()
                        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                    }
                    .show()
            }
        }
    }

}