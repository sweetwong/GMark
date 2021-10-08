package sweet.wong.gmark.repolist

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.R
import sweet.wong.gmark.data.Repo
import sweet.wong.gmark.databinding.RecycleItemRepoBinding
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.DefaultDiffUtilCallback
import sweet.wong.gmark.utils.Event

/**
 * Repository item adapter
 */
class RepoListAdapter(
    private val viewModel: RepoListViewModel,
    private val lifecycleOwner: LifecycleOwner
) : ListAdapter<RepoUIState, RepoListAdapter.VH>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemRepoBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(viewModel, getItem(position))

    inner class VH(private val binding: RecycleItemRepoBinding) : ViewHolder(binding.root) {

        fun bind(viewModel: RepoListViewModel, uiState: RepoUIState) {
            uiState.bind(lifecycleOwner) {
                binding.state = uiState
                binding.viewModel = viewModel
                binding.executePendingBindings()

                if (uiState.repo.state == Repo.STATE_SYNCING) {
                    binding.btnSync.start()
                } else {
                    binding.btnSync.stop()
                }
            }

            itemView.setOnLongClickListener { v ->
                showPopMenu(v, uiState)
                true
            }

            itemView.setOnClickListener {
                viewModel.repoSelectEvent.value = Event(uiState.repo)
            }
        }

        private fun showPopMenu(v: View, uiState: RepoUIState) {
            val popupMenu = PopupMenu(v.context, v, Gravity.END)
            popupMenu.inflate(R.menu.popup_menu_repo_list)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_pull -> {
                    }
                    R.id.menu_delete -> viewModel.deleteRepo(uiState)
                }
                true
            }
            popupMenu.show()
        }

    }

}