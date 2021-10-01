package sweet.wong.gmark.repo

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.repo.viewholder.MarkdownViewHolder
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class RepoPageAdapter(
    private val activity: AppCompatActivity,
    private val repoViewModel: RepoViewModel
) : ListAdapter<Page, MarkdownViewHolder>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MarkdownViewHolder.create(activity, repoViewModel, parent)

    override fun onBindViewHolder(holder: MarkdownViewHolder, position: Int) =
        holder.bind(getItem(position))

}