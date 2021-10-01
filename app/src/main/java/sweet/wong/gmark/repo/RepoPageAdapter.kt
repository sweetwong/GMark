package sweet.wong.gmark.repo

import android.view.KeyEvent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ListAdapter
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.data.PageType
import sweet.wong.gmark.repo.viewholder.AbsViewHolder
import sweet.wong.gmark.repo.viewholder.MarkdownViewHolder
import sweet.wong.gmark.repo.viewholder.WebViewViewHolder
import sweet.wong.gmark.utils.DefaultDiffUtilCallback
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList

class RepoPageAdapter(
    private val activity: AppCompatActivity,
    private val repoViewModel: RepoViewModel
) : ListAdapter<Page, AbsViewHolder<Page>>(DefaultDiffUtilCallback()) {

    private val weakViewHolders = CopyOnWriteArrayList<WeakReference<AbsViewHolder<Page>>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsViewHolder<Page> {
        val viewHolder = when (viewType) {
            VIEW_TYPE_WEB_VIEW -> WebViewViewHolder.create(activity, repoViewModel, parent)
            else -> MarkdownViewHolder.create(activity, repoViewModel, parent)
        }
        weakViewHolders.forEach { weakViewHolder ->
            if (weakViewHolder.get() == null) {
                weakViewHolders.remove(weakViewHolder)
            }
        }
        weakViewHolders.add(WeakReference(viewHolder))
        return viewHolder
    }

    override fun onBindViewHolder(holder: AbsViewHolder<Page>, position: Int) =
        holder.bind(getItem(position))

    override fun getItemViewType(position: Int) = when (getItem(position).pageType) {
        PageType.FILE -> VIEW_TYPE_MARKDOWN
        PageType.URL -> VIEW_TYPE_WEB_VIEW
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        weakViewHolders.forEach {
            if (it.get()?.onKeyUp(keyCode, event) == true) {
                return true
            }
        }
        return false
    }

    companion object {
        private const val VIEW_TYPE_MARKDOWN = 0
        private const val VIEW_TYPE_WEB_VIEW = 1
    }

}