package sweet.wong.gmark.repo.drawer.project

import android.view.*
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.view.inputmethod.EditorInfo.IME_ACTION_UNSPECIFIED
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.blankj.utilcode.util.KeyboardUtils
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.databinding.RecycleItemProjectBinding
import sweet.wong.gmark.ext.getColorFromAttr
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

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
            // Observer refresh event
            uiState.bind(viewLifecycleOwner) {
                this.uiState = uiState
                executePendingBindings()

                // Binding text color
                tvName.setTextColor(if (uiState.isHighlight) textHighlightColor else textMainColor)

                if (uiState.isEditing) {
                    etRename.setSelection(etRename.length())
                    KeyboardUtils.showSoftInput(etRename)
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
            popupMenu.inflate(R.menu.popup_menu_project_browser)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_new_file -> clickNewFile(uiState)
                    R.id.menu_new_folder -> clickNewFolder(uiState)
                    R.id.menu_rename -> clickRename(uiState)
                    R.id.menu_delete -> clickDelete(uiState)
                }
                true
            }
            popupMenu.show()
        }

        private fun clickNewFile(uiState: ProjectUIState) {

        }

        private fun clickNewFolder(uiState: ProjectUIState) {

        }

        private fun clickDelete(uiState: ProjectUIState) {
            viewModel.delete(uiState)
        }

        private fun clickRename(uiState: ProjectUIState) = with(binding) {
            uiState.isEditing = true
            uiState.updateUI()

            etRename.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == IME_ACTION_DONE || actionId == IME_ACTION_UNSPECIFIED) {
                    viewModel.rename(uiState)
                    KeyboardUtils.hideSoftInput(etRename)
                    return@setOnEditorActionListener true
                }
                false
            }
        }

    }

}