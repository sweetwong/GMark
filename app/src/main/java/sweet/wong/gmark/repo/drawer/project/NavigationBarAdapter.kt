package sweet.wong.gmark.repo.drawer.project

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.databinding.RecycleNavigationBarBinding
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class NavigationBarAdapter(private val onItemClick: (ProjectUIState) -> Unit) :
    ListAdapter<ProjectUIState, NavigationBarAdapter.VH>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(RecycleNavigationBarBinding.inflate(parent.inflater, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position), position)

    inner class VH(private val binding: RecycleNavigationBarBinding) : ViewHolder(binding.root) {

        fun bind(uiState: ProjectUIState, position: Int) {
            binding.file = uiState.drawerFile
            binding.showArrow = position != 0
            binding.executePendingBindings()

            itemView.setOnClickListener {
                onItemClick(
                    ProjectUIState(
                        uiState.drawerFile,
                        uiState.showingFile,
                        uiState.rootFile
                    )
                )
            }
        }

    }

}