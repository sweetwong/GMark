package sweet.wong.gmark.repolist

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.gmark.R
import sweet.wong.gmark.databinding.RecycleItemRepoBinding
import sweet.wong.gmark.utils.Event

/**
 * TODO: Add Description
 */
class RepoListAdapter(private val viewModel: RepoListViewModel) :
    ListAdapter<RepoUIState, RepoListAdapter.VH>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH.from(parent)

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(viewModel, getItem(position))

    class VH(private val binding: RecycleItemRepoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: RepoListViewModel, uiState: RepoUIState) {
            val refresh = {
                binding.state = uiState
                binding.executePendingBindings()
            }
            refresh()
            uiState.refresh = refresh

            itemView.setOnLongClickListener { v ->
                val popupMenu = PopupMenu(v.context, v, Gravity.END)
                popupMenu.inflate(R.menu.popup_menu_repo_list)
                popupMenu.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.menu_pull -> {
//                            viewModel.pull(uiState)
                        }
                        R.id.menu_delete -> viewModel.deleteRepo(uiState)
                    }
                    true
                }
                popupMenu.show()
                true
            }

            itemView.setOnClickListener {
                viewModel.repoSelectEvent.value = Event(uiState.repo)
            }
        }

        companion object {

            fun from(parent: ViewGroup) = VH(
                RecycleItemRepoBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

        }
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<RepoUIState>() {

            override fun areItemsTheSame(oldItem: RepoUIState, newItem: RepoUIState): Boolean {
                return oldItem.repo == newItem.repo
            }

            override fun areContentsTheSame(oldItem: RepoUIState, newItem: RepoUIState): Boolean {
                return oldItem.repo == newItem.repo
            }

        }

    }

}