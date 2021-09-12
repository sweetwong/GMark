package sweet.wong.gmark.repo.outline

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.RecycleItemOutlineBinding
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.repo.viewmodel.MarkdownViewModel
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class OutlineAdapter(private val markdownViewModel: MarkdownViewModel) :
    ListAdapter<Head, OutlineAdapter.VH>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemOutlineBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(getItem(position))

    inner class VH(private val binding: RecycleItemOutlineBinding) : ViewHolder(binding.root) {

        fun bind(head: Head) {
            binding.head = head
            binding.executePendingBindings()

            itemView.setOnClickListener {
            }

            binding.spinner.setOnClickListener {
                rotateSpin(it, head.spinOpened == true)
                head.spinOpened = head.spinOpened == false
                markdownViewModel.selectSpinner(head)
            }
        }

        private fun rotateSpin(spin: View, spinOpened: Boolean) {
            toast("spinOpened", spinOpened)
            val anim = RotateAnimation(
                if (spinOpened) 0f else -90f,
                if (spinOpened) -90f else 0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            anim.duration = 300
            anim.interpolator = LinearInterpolator()
            anim.fillAfter = true
            spin.startAnimation(anim)
        }

    }
}