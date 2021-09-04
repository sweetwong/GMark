package sweet.wong.gmark.repo.markdown

import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonVisitor
import sweet.wong.gmark.repo.RepoViewModel

class LinkPlugin(private val viewModel: RepoViewModel) : AbstractMarkwonPlugin() {

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {
        builder.linkResolver(LinkResolver(viewModel))
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        super.configureVisitor(builder)
    }

    companion object {

        fun create(viewModel: RepoViewModel): LinkPlugin {
            return LinkPlugin(viewModel)
        }

    }

}