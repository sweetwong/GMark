package sweet.wong.gmark.filepreview.markdown

import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.filepreview.FilePreviewViewModel

class MarkdownDelegate(viewModel: FilePreviewViewModel) {

    private val markwon: Markwon = Markwon.builder(App.app)
        .usePlugins(
            listOf(
                GlideImagesPlugin.create(App.app),
                HtmlPlugin.create(),
                TablePlugin.create(App.app),
                LinkPlugin.create(viewModel)
            )
        )
        .build()

    val adapter = MarkwonAdapter.createTextViewIsRoot(R.layout.adapter_default_entry)

    fun setMarkdown(markList: RecyclerView, markdown: String) {
        markList.adapter = adapter
        val node = markwon.parse(markdown)
        adapter.setParsedMarkdown(markwon, node)
        adapter.notifyDataSetChanged()
    }


}