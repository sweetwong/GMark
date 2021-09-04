package sweet.wong.gmark.repo.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.gmark.R
import sweet.wong.gmark.core.resources
import sweet.wong.gmark.databinding.RecycleItemProjectBinding
import sweet.wong.gmark.repo.RepoViewModel
import sweet.wong.gmark.utils.Resources
import java.io.File

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/1
 */
class RepoDrawerProjectAdapter(private val viewModel: RepoViewModel) :
    ListAdapter<File, RepoDrawerProjectAdapter.VH>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH.from(parent)

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(viewModel, getItem(position))

    fun onBackPressed(): Boolean {
        if (viewModel.currentProjectFolder.value?.absolutePath == viewModel.repo?.localPath) {
            return false
        }
        viewModel.currentProjectFolder.value?.parentFile?.let {
            if (it.exists()) {
                viewModel.currentProjectFolder.value = it
                return true
            }
        }
        return false
    }

    class VH(private val binding: RecycleItemProjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: RepoViewModel, childFile: File) {
            binding.file = childFile
            binding.executePendingBindings()
            binding.text.setTextColor(resources.getColor(R.color.text_main, null))

            if (childFile.isDirectory) {
                binding.icon.setImageResource(R.drawable.folder)
                setFolderHighlight(viewModel.currentFile, childFile, File(viewModel.repo.localPath))
            }
            if (childFile.isFile) {
                binding.icon.setImageResource(R.drawable.text)
                setFileHighlight(viewModel.currentFile, childFile)
            }

            itemView.setOnClickListener {
                if (childFile.isDirectory) {
                    viewModel.currentProjectFolder.value = childFile
                }
                if (childFile.isFile) {
                    viewModel.selectFile(childFile)
                }
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

        companion object {

            fun from(parent: ViewGroup) = VH(
                RecycleItemProjectBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        }

    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<File>() {
            override fun areItemsTheSame(
                oldItem: File,
                newItem: File
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: File,
                newItem: File
            ): Boolean {
                return oldItem.absolutePath == newItem.absolutePath
            }
        }

    }

}