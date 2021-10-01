package sweet.wong.gmark.repo

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.data.PageType
import sweet.wong.gmark.repo.viewholder.AbsViewHolder
import sweet.wong.gmark.repo.viewholder.MarkdownViewHolder
import sweet.wong.gmark.repo.viewholder.WebViewViewHolder
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class RepoPageAdapter(
    private val activity: AppCompatActivity,
    private val repoViewModel: RepoViewModel
) : ListAdapter<Page, AbsViewHolder<Page>>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            VIEW_TYPE_WEB_VIEW -> WebViewViewHolder.create(activity, repoViewModel, parent)
            else -> MarkdownViewHolder.create(activity, repoViewModel, parent)
        }

    override fun onBindViewHolder(holder: AbsViewHolder<Page>, position: Int) =
        holder.bind(getItem(position))

    override fun getItemViewType(position: Int) = when (getItem(position).pageType) {
        PageType.FILE -> VIEW_TYPE_MARKDOWN
        PageType.URL -> VIEW_TYPE_WEB_VIEW
    }

    companion object {
        private const val VIEW_TYPE_MARKDOWN = 0
        private const val VIEW_TYPE_WEB_VIEW = 1
    }

}