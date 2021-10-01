package sweet.wong.gmark.repo.drawer.git

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.databinding.RecycleItemGitDiffBinding
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
        }

    }

}