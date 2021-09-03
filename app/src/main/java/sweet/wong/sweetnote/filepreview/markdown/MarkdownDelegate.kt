package sweet.wong.sweetnote.filepreview.markdown

import android.widget.TextView
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import sweet.wong.sweetnote.core.App
import sweet.wong.sweetnote.filepreview.FilePreviewViewModel

class MarkdownDelegate(viewModel: FilePreviewViewModel) {

    private val markwon: Markwon = Markwon.builder(App.app)
        .usePlugins(
            listOf(
                GlideImagesPlugin.create(App.app),
                HtmlPlugin.create(),
                LinkPlugin.create(viewModel)
            )
        )
        .build()

    fun setMarkdown(textView: TextView, markdown: String) {
        markwon.setMarkdown(textView, markdown)
    }


}