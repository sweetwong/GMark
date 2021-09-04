package sweet.wong.sweetnote.filepreview.markdown

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonVisitor
import sweet.wong.sweetnote.filepreview.FilePreviewViewModel

class LinkPlugin(private val viewModel: FilePreviewViewModel) : AbstractMarkwonPlugin() {

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.linkResolver(LinkResolver(viewModel))
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        super.configureVisitor(builder)
    }

    companion object {

        fun create(viewModel: FilePreviewViewModel): LinkPlugin {
            return LinkPlugin(viewModel)
        }

    }

}