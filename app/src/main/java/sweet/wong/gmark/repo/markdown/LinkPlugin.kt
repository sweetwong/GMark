package sweet.wong.gmark.repo.markdown

import android.view.View
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.LinkResolverDef
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonVisitor
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.repo.RepoViewModel
import java.io.File

class LinkPlugin(private val viewModel: RepoViewModel) : AbstractMarkwonPlugin() {

    override fun configureConfiguration(builder: MarkwonConfiguration.Builder) {

        builder.linkResolver(object : LinkResolverDef() {
            override fun resolve(view: View, link: String) {
                if (link.startsWith("http")) {
                    super.resolve(view, link)
                    return
                }

                val showingFile = viewModel.showingFile ?: return toast("Current file is null")

                log("link is $link", "showingFile is $showingFile")

                val folder = showingFile.parentFile
                if (folder == null || !folder.exists()) return toast("Folder is null")

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

                viewModel.selectFile(File(resolved))

            }
        })
    }

}