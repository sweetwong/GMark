package sweet.wong.gmark.repo.drawer.history

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.databinding.RecycleItemHistoryBinding
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class HistoryAdapter(private val viewModel: HistoryViewModel) :
    ListAdapter<Page, HistoryAdapter.VH>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemHistoryBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: RecycleItemHistoryBinding) : ViewHolder(binding.root) {

        fun bind(page: Page) {
            binding.viewModel = viewModel
            binding.page = page
            binding.executePendingBindings()
        }

    }
}