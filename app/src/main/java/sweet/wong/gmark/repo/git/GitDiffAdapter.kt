package sweet.wong.gmark.repo.git

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import org.eclipse.jgit.diff.DiffEntry
import sweet.wong.gmark.databinding.RecycleItemGitDiffBinding
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class GitDiffAdapter : ListAdapter<DiffEntry, GitDiffAdapter.VH>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemGitDiffBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(private val binding: RecycleItemGitDiffBinding) : ViewHolder(binding.root) {

        fun bind(diffEntry: DiffEntry) {
            binding.entry = diffEntry
            binding.executePendingBindings()
        }

    }

}