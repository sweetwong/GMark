package sweet.wong.sweetnote.repolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        private val lifecycleOwner: LifecycleOwner = (itemView.context as AppCompatActivity)

        fun bind(uiState: RepoUIState) {
            uiState.progress.observe(lifecycleOwner) {
                repoNameText.text = it.toString()
            }

            repoNameText.setOnClickListener {
                uiState.progress.value = uiState.progress.value + 1
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