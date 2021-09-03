package sweet.wong.sweetnote.filepreview.drawer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.sweetnote.databinding.RecycleItemProjectBinding
import sweet.wong.sweetnote.filepreview.FilePreviewViewModel
import java.io.File

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/1
 */
class DrawerProjectAdapter(private val viewModel: FilePreviewViewModel) :
    ListAdapter<File, DrawerProjectAdapter.VH>(diffCallback) {

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

        fun bind(viewModel: FilePreviewViewModel, childFile: File) {
            binding.file = childFile
            binding.executePendingBindings()

            itemView.setOnClickListener {
                if (childFile.isDirectory) {
                    viewModel.currentProjectFolder.value = childFile
                }
                if (childFile.isFile) {
                    viewModel.selectFile(childFile)
                }
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