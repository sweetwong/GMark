package sweet.wong.sweetnote.filepreview.markdown

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import sweet.wong.sweetnote.filepreview.FilePreviewViewModel

class LinkPlugin(private val viewModel: FilePreviewViewModel) : AbstractMarkwonPlugin() {

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.linkResolver(LinkResolver(viewModel))
    }

    companion object {

        fun create(viewModel: FilePreviewViewModel): LinkPlugin {
            return LinkPlugin(viewModel)
        }

    }

}