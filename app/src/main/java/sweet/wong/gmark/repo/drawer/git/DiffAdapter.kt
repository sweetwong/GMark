package sweet.wong.gmark.repo.drawer.git

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.R
import sweet.wong.gmark.databinding.RecycleItemGitDiffBinding
import sweet.wong.gmark.diff.DiffActivity
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class DiffAdapter(
    private val viewModel: GitViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<DiffUIState, DiffAdapter.VH>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemGitDiffBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: RecycleItemGitDiffBinding) : ViewHolder(binding.root) {

        fun bind(uiState: DiffUIState) {
            uiState.bind(lifecycleOwner) {
                binding.uiState = uiState
                binding.executePendingBindings()
            }

            itemView.setOnClickListener {
                DiffActivity.start(itemView.context, viewModel.repo.root, uiState.entry.newPath)
            }

            itemView.setOnLongClickListener {
                showPopupMenu(it, uiState)
                true
            }
        }

        private fun showPopupMenu(v: View, uiState: DiffUIState) = with(binding) {
            val popupMenu = PopupMenu(v.context, v, Gravity.END)
            popupMenu.inflate(R.menu.popup_menu_diff_item)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_rollback -> {
                        viewModel.rollback(uiState)
                    }
                }
                true
            }
            popupMenu.show()
        }


    }

}