package sweet.wong.gmark.repo.viewholder

import android.view.KeyEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import me.jingbin.web.ByWebView
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.databinding.PageWebviewBinding
import sweet.wong.gmark.ext.getColorFromAttr
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.repo.RepoViewModel
import sweet.wong.gmark.webview.MyWebView

class WebViewViewHolder(
    private val activity: AppCompatActivity,
    private val viewModel: RepoViewModel,
    private val binding: PageWebviewBinding
) : AbsViewHolder<Page>(binding.root) {

    private var byWebView: ByWebView? = null

    override fun bind(data: Page) {
        binding.webViewContainer.removeAllViews()

        byWebView = ByWebView.with(activity)
            .setWebParent(
                binding.webViewContainer,
                ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            )
            .setCustomWebView(MyWebView(activity))
            .useWebProgress(App.app.getColorFromAttr(R.attr.colorPrimary))
            .loadUrl(data.path)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return adapterPosition == viewModel.currentPosition
                && byWebView?.handleKeyEvent(keyCode, event) == true
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