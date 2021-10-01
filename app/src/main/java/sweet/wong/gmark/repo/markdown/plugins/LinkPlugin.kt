package sweet.wong.gmark.repo.markdown.plugins

import android.view.View
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolverDef
import io.noties.markwon.MarkwonConfiguration
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import java.io.File

class LinkPlugin(
    private val getShowingFile: () -> File?,
    private val getNodes: () -> List<Node>,
    private val onSelectFile: (File) -> Unit,
    private val onClickCatalog: (Int) -> Unit
) : AbstractMarkwonPlugin() {

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {

        builder.linkResolver(object : LinkResolverDef() {
            override fun resolve(view: View, link: String) {
                if (link.startsWith("http")) {
                    super.resolve(view, link)
                    return
                }

                val showingFile = getShowingFile() ?: return toast("Current file is null")

                log("link is $link", "showingFile is $showingFile")

                val folder = showingFile.parentFile
                if (folder == null || !folder.exists()) return toast("Folder is null")

                if (link.startsWith("#")) {
                    val head = link.replace("#", "")
                    val nodes = getNodes()
                    repeat(nodes.size) { i ->
                        val node = nodes[i]
                        val firstChild = node.firstChild
                        if (node is Heading && firstChild is Text && firstChild.literal == head) {
                            onClickCatalog(i)
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

                onSelectFile(File(resolved))
            }
        })
    }

}