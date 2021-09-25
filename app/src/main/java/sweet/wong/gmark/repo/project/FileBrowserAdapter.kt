package sweet.wong.gmark.repo.project

import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.core.resources
import sweet.wong.gmark.databinding.RecycleItemProjectBinding
import sweet.wong.gmark.ext.getColorFromAttr
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.DefaultDiffUtilCallback
import sweet.wong.gmark.utils.EventObserver
import java.io.File

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/1
 */
class FileBrowserAdapter(
    private val viewModel: ProjectViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val onItemClick: (ProjectUIState) -> Unit
) :
    ListAdapter<ProjectUIState, FileBrowserAdapter.VH>(DefaultDiffUtilCallback()) {

    private val textMainColor = App.app.getColor(R.color.text_main)
    private val textHighlightColor = App.app.getColorFromAttr(R.attr.colorPrimary)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemProjectBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: RecycleItemProjectBinding) : ViewHolder(binding.root) {

        fun bind(uiState: ProjectUIState) = with(binding) {
            this.uiState = uiState
            executePendingBindings()
            tvName.setTextColor(resources.getColor(R.color.text_main, null))

            when {
                uiState.navigateBack -> {
                    ivIcon.setImageResource(R.drawable.folder)
                    tvName.setTextColor(textMainColor)
                }
                uiState.drawerFile.isDirectory -> {
                    ivIcon.setImageResource(R.drawable.folder)
                    setFolderHighlight(uiState.showingFile, uiState.drawerFile, uiState.rootFile)
                }
                uiState.drawerFile.isFile -> {
                    ivIcon.setImageResource(R.drawable.text)
                    setFileHighlight(uiState.showingFile, uiState.drawerFile)
                }
            }

            itemView.setOnClickListener {
                onItemClick(uiState)
            }

            itemView.setOnLongClickListener {
                showPopupMenu(it, uiState)
                true
            }
        }

        private fun showPopupMenu(v: View, uiState: ProjectUIState) = with(binding) {
            val popupMenu = PopupMenu(v.context, v, Gravity.END)
            popupMenu.inflate(R.menu.menu_project_browser)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_new -> {

                    }
                    R.id.menu_rename -> {
                        clickRename(uiState)
                    }
                }
                true
            }
            popupMenu.show()
        }

        private fun clickRename(uiState: ProjectUIState) = with(binding) {
            fun rename() {
                tvName.isVisible = true
                etRename.isVisible = false
                val newName = etRename.text.toString()
                viewModel.renameFile(uiState.drawerFile, newName)
                    .observe(viewLifecycleOwner, EventObserver { success ->
                        if (success) {
                            tvName.text = newName
                            viewModel.refreshDrawer()
                        }
                    })
            }

            tvName.isVisible = false
            etRename.isVisible = true
            etRename.setText(tvName.text)
            etRename.requestFocus()

            etRename.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                ) {
                    rename()
                    return@setOnEditorActionListener true
                }
                false
            }

            etRename.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    rename()
                }
            }

        }

        private fun setFolderHighlight(file: File?, targetFile: File, rootFile: File?) {
            if (file == null || !file.exists() || file == rootFile) {
                binding.tvName.setTextColor(textMainColor)
                return
            }

            if (file == targetFile) {
                binding.tvName.setTextColor(textHighlightColor)
                return
            }

            setFolderHighlight(file.parentFile, targetFile, rootFile)
        }

        private fun setFileHighlight(file: File?, targetFile: File) {
            if (file == targetFile) {
                binding.tvName.setTextColor(textHighlightColor)
            } else {
                binding.tvName.setTextColor(textMainColor)
            }
        }

    }

}