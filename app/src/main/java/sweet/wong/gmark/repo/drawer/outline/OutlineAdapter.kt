package sweet.wong.gmark.repo.drawer.outline

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.databinding.RecycleItemOutlineBinding
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.repo.viewmodel.MarkdownViewModel
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class OutlineAdapter(markdownViewModel: MarkdownViewModel) :
    ListAdapter<String, OutlineAdapter.VH>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemOutlineBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position), position)

    inner class VH(private val binding: RecycleItemOutlineBinding) : ViewHolder(binding.root) {

        fun bind(item: String, position: Int) {

        }

    }
}