package sweet.wong.gmark.repo.viewholder

import android.animation.ObjectAnimator
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import me.jingbin.web.ByWebView
import me.jingbin.web.OnTitleProgressCallback
import sweet.wong.gmark.data.Page
import sweet.wong.gmark.databinding.PageWebviewBinding
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.repo.RepoViewModel
import sweet.wong.gmark.utils.Event
import sweet.wong.gmark.webview.MyWebView

class WebViewViewHolder(
    private val activity: AppCompatActivity,
    private val viewModel: RepoViewModel,
    private val binding: PageWebviewBinding
) : AbsViewHolder<Page>(binding.root) {

    private var byWebView: ByWebView? = null

    private val onTabReselectObserver = Observer<Unit> {
        val webView = byWebView?.webView ?: return@Observer
        webView.scrollTo(webView.scrollX, webView.scrollY)
        val anim = ObjectAnimator.ofInt(webView, "scrollY", webView.scrollY, 0)
        anim.setDuration(200).start()
    }

    override fun bind(data: Page) {
        binding.webViewContainer.removeAllViews()

        byWebView = ByWebView.with(activity)
            .setWebParent(
                binding.webViewContainer,
                ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            )
            .setCustomWebView(MyWebView(activity))
            .setOnTitleProgressCallback(object : OnTitleProgressCallback() {
                override fun onReceivedTitle(title: String) {
                    data.name = title
                    viewModel.webViewNameUpdateEvent.value = Event(title)
                }
            })
            .useWebProgress(false)
            .loadUrl(data.path)

        viewModel.onTabReselect.removeObserver(onTabReselectObserver)
        viewModel.onTabReselect.observe(activity, onTabReselectObserver)
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