package sweet.wong.sweetnote.repolist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.sweetnote.core.Event
import sweet.wong.sweetnote.databinding.RecyclerItemRepoBinding

/**
 * TODO: Add Description
 */
class RepoListAdapter(private val viewModel: RepoListViewModel) :
    ListAdapter<RepoUIState, RepoListAdapter.VH>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH.from(parent)

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(viewModel, getItem(position))

    class VH(private val binding: RecyclerItemRepoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: RepoListViewModel, uiState: RepoUIState) {
            binding.state = uiState
            binding.executePendingBindings()

            itemView.setOnLongClickListener {
                viewModel.deleteRepo(uiState.repo)
                true
            }

            itemView.setOnClickListener {
                viewModel.repoSelectEvent.value = Event(uiState.repo)
            }
        }

        companion object {

            fun from(parent: ViewGroup) = VH(
                RecyclerItemRepoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )

        }
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<RepoUIState>() {

            override fun areItemsTheSame(oldItem: RepoUIState, newItem: RepoUIState): Boolean {
                return oldItem.repo.uid == newItem.repo.uid
            }

            override fun areContentsTheSame(oldItem: RepoUIState, newItem: RepoUIState): Boolean {
                return oldItem.repo.uid == newItem.repo.uid
            }

        }

    }

}