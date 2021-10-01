package sweet.wong.gmark.repo.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class AbsViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(data: T)

}