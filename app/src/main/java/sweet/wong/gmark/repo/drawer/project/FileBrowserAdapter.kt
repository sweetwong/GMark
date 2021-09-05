package sweet.wong.gmark.repo.drawer.project

import android.os.FileUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.R
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.resources
import sweet.wong.gmark.databinding.RecycleItemProjectBinding
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.Resources
import java.io.File

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/1
 */
class FileBrowserAdapter(private val onItemClick: (ProjectUIState) -> Unit) :
    ListAdapter<ProjectUIState, FileBrowserAdapter.VH>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemProjectBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: RecycleItemProjectBinding) : ViewHolder(binding.root) {

        fun bind(uiState: ProjectUIState) {
            binding.file = uiState.drawerFile
            binding.executePendingBindings()
            binding.text.setTextColor(resources.getColor(R.color.text_main, null))

            if (uiState.drawerFile.isDirectory) {
                binding.icon.setImageResource(R.drawable.folder)
                setFolderHighlight(uiState.currentFile, uiState.drawerFile, uiState.rootFile)
            }
            if (uiState.drawerFile.isFile) {
                binding.icon.setImageResource(R.drawable.text)
                setFileHighlight(uiState.currentFile, uiState.drawerFile)
            }

            itemView.setOnClickListener {
                onItemClick(ProjectUIState(uiState.drawerFile, uiState.currentFile, uiState.rootFile))
            }
        }

        private fun setFolderHighlight(file: File?, targetFile: File, rootFile: File?) {
            if (file == null || !file.exists() || file == rootFile) {
                binding.text.setTextColor(Resources.COLOR_TEXT_MAIN)
                return
            }

            if (file == targetFile) {
                binding.text.setTextColor(Resources.COLOR_TEXT_HIGHLIGHT)
                return
            }

            setFolderHighlight(file.parentFile, targetFile, rootFile)
        }

        private fun setFileHighlight(file: File?, targetFile: File) {
            if (file == targetFile) {
                binding.text.setTextColor(Resources.COLOR_TEXT_HIGHLIGHT)
            } else {
                binding.text.setTextColor(Resources.COLOR_TEXT_MAIN)
            }
        }

    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<ProjectUIState>() {

            override fun areItemsTheSame(old: ProjectUIState, new: ProjectUIState): Boolean {
                log("FileBrowserAdapter", "old.drawerFile === new.drawerFile", old.drawerFile === new.drawerFile)
                return old.drawerFile.path == new.drawerFile.path
            }

            override fun areContentsTheSame(old: ProjectUIState, new: ProjectUIState): Boolean {
                log("FileBrowserAdapter", "old.drawerFile == new.drawerFile", old.drawerFile == new.drawerFile)
                return old.drawerFile.path == new.drawerFile.path
            }
        }

    }

}