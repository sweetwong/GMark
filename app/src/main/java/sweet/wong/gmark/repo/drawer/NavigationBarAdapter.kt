package sweet.wong.gmark.repo.drawer

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.databinding.RecycleNavigationBarBinding
import sweet.wong.gmark.ext.layoutInflater
import java.io.File

class NavigationBarAdapter :
    ListAdapter<File, NavigationBarAdapter.VH>(diffCallback) {

    var onItemClick: ((File) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(RecycleNavigationBarBinding.inflate(parent.layoutInflater, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position), position)

    inner class VH(private val binding: RecycleNavigationBarBinding) : ViewHolder(binding.root) {

        fun bind(file: File, position: Int) {
            binding.file = file
            binding.showArrow = position != 0
            binding.executePendingBindings()

            itemView.setOnClickListener {
                onItemClick?.let { it(file) }
            }
        }

    }

    companion object {

        private val diffCallback = object : DiffUtil.ItemCallback<File>() {

            override fun areItemsTheSame(oldItem: File, newItem: File): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: File, newItem: File): Boolean {
                return oldItem.absolutePath == newItem.absolutePath
            }
        }

    }


}