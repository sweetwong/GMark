package sweet.wong.sweetnote.repolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.sweetnote.R

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/2
 */
class RepoListAdapter : ListAdapter<RepoUIState, RepoListAdapter.VH>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH.from(parent)

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val repoNameText: TextView = itemView.findViewById(R.id.repo_name_text)
        private val tipText: TextView = itemView.findViewById(R.id.tip_text)
        private val progressText: TextView = itemView.findViewById(R.id.progress_text)

        private val activity: AppCompatActivity = (itemView.context as AppCompatActivity)
        private val viewModel: RepoListViewModel by activity.viewModels()

        /**
         * Bind UI here
         */
        fun bind(uiState: RepoUIState) {
            uiState.repo.observe(activity) {
                repoNameText.text = it.name
            }
            uiState.progress.observe(activity) {
                progressText.text = it.toString()
            }
            uiState.tipText.observe(activity) {
                tipText.text = it
            }

            itemView.setOnLongClickListener {
                viewModel.deleteRepo(uiState.repo.value)
                true
            }
        }

        companion object {

            fun from(parent: ViewGroup) = VH(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_item_repo_list, parent, false)
            )

        }
    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<RepoUIState>() {
            override fun areItemsTheSame(
                oldItem: RepoUIState,
                newItem: RepoUIState
            ): Boolean {
                return oldItem.repo.value.uid == newItem.repo.value.uid
            }

            override fun areContentsTheSame(
                oldItem: RepoUIState,
                newItem: RepoUIState
            ): Boolean {
                return oldItem.repo.value.uid == newItem.repo.value.uid
            }
        }

    }

}