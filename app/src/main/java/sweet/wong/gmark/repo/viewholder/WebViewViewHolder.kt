package sweet.wong.gmark.repo.viewholder

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import me.jingbin.web.ByWebView
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.databinding.PageWebviewBinding
import sweet.wong.gmark.ext.getColorFromAttr
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.repo.RepoViewModel

class WebViewViewHolder(
    activity: AppCompatActivity,
    viewModel: RepoViewModel,
    private val binding: PageWebviewBinding
) : AbsViewHolder<Page>(binding.root) {

    private val byWebView: ByWebView = ByWebView.with(activity)
        .setWebParent(
            binding.webViewContainer, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        .useWebProgress(App.app.getColorFromAttr(R.attr.colorPrimary))
        .loadUrl(null)

    override fun bind(data: Page) {
        byWebView.loadUrl(data.path)
    }

    companion object {

        fun create(activity: AppCompatActivity, viewModel: RepoViewModel, parent: ViewGroup) =
            WebViewViewHolder(
                activity,
                viewModel,
                PageWebviewBinding.inflate(parent.inflater, parent, false)
            )


    }
}