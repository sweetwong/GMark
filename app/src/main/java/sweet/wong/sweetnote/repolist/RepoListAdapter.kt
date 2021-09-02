package sweet.wong.sweetnote.repolist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.core.NonNullLiveData
import sweet.wong.sweetnote.data.Repo
import sweet.wong.sweetnote.ext.lifecycleOwner

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/2
 */
class RepoListAdapter : ListAdapter<NonNullLiveData<Repo>, RepoListAdapter.VH>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH.from(parent)

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val repoNameText: TextView = itemView.findViewById(R.id.repo_name_text)

        fun bind(repo: NonNullLiveData<Repo>) {
            repoNameText.text = repo.value.name

            repo.observe(itemView.lifecycleOwner) {
                repoNameText.text = it.name
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

        private val diffCallback = object : DiffUtil.ItemCallback<NonNullLiveData<Repo>>() {
            override fun areItemsTheSame(
                oldItem: NonNullLiveData<Repo>,
                newItem: NonNullLiveData<Repo>
            ): Boolean {
                return oldItem.value.uid == newItem.value.uid
            }

            override fun areContentsTheSame(
                oldItem: NonNullLiveData<Repo>,
                newItem: NonNullLiveData<Repo>
            ): Boolean {
                return oldItem.value.name == newItem.value.name
            }
        }

    }

}