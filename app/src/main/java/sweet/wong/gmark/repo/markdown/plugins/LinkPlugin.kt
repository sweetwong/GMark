package sweet.wong.gmark.repo.markdown.plugins

import android.view.View
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolverDef
import io.noties.markwon.MarkwonConfiguration
import org.commonmark.node.Heading
import org.commonmark.node.Text
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.repo.markdown.MarkdownDelegate
import sweet.wong.gmark.repo.viewmodel.MarkdownViewModel
import sweet.wong.gmark.repo.viewmodel.RepoViewModel
import java.io.File

class LinkPlugin(
    private val repoViewModel: RepoViewModel,
    private val markdownViewModel: MarkdownViewModel,
    private val delegate: MarkdownDelegate,
) : AbstractMarkwonPlugin() {

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {

        builder.linkResolver(object : LinkResolverDef() {
            override fun resolve(view: View, link: String) {
                if (link.startsWith("http")) {
                    super.resolve(view, link)
                    return
                }

                val showingFile = repoViewModel.showingFile ?: return toast("Current file is null")

                log("link is $link", "showingFile is $showingFile")

                val folder = showingFile.parentFile
                if (folder == null || !folder.exists()) return toast("Folder is null")

                if (link.startsWith("#")) {
                    val head = link.replace("#", "")
                    val nodes = markdownViewModel.nodes.value ?: return
                    val recyclerView = delegate.markList
                    repeat(nodes.size) { i ->
                        val node = nodes[i]
                        val firstChild = node.firstChild
                        if (node is Heading && firstChild is Text && firstChild.literal == head) {
                            recyclerView.smoothScrollToPosition(i)
                        }
                    }
                    return
                }

                val resolved = when {
                    link.startsWith("./") -> {
                        folder.absolutePath + "/" + link.replaceFirst("./", "")
                    }
                    link.startsWith("../") -> {
                        folder.parentFile?.absolutePath + "/" + link.replaceFirst("./", "")
                    }
                    link.startsWith("/") -> {
                        folder.absolutePath + link
                    }
                    else -> {
                        folder.absolutePath + "/" + link
                    }
                }

                repoViewModel.selectFile(File(resolved))

            }
        })
    }

}