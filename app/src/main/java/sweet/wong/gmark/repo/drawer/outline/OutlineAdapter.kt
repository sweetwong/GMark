package sweet.wong.gmark.repo.drawer.outline

import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.core.noOpDelegate
import sweet.wong.gmark.databinding.RecycleItemOutlineBinding
import sweet.wong.gmark.ext.dp
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.repo.markdown.MarkdownViewModel
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

            itemView.setPadding(16.dp * (head.level - 1), 6.dp, 0, 6.dp)

            binding.spinner.setOnClickListener {
                val old = head.spinOpened ?: return@setOnClickListener
                val new = !old

                rotateSpin(binding.spinner, head, new)
                head.spinOpened = new

                markdownViewModel.selectSpinner(head)
            }
        }

        private fun rotateSpin(spin: ImageView, head: Head, new: Boolean) {
            val anim = RotateAnimation(
                if (new) 0f else 0f,
                if (new) 90f else -90f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            anim.duration = 300
            anim.interpolator = LinearInterpolator()
            spin.startAnimation(anim)
            anim.setAnimationListener(object : Animation.AnimationListener by noOpDelegate() {

                override fun onAnimationEnd(animation: Animation?) {
                    binding.head = head
                    binding.executePendingBindings()
                }

            })
        }

    }
}