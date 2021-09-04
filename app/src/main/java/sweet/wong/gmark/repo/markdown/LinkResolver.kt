package sweet.wong.gmark.repo.markdown

import android.view.View
import io.noties.markwon.LinkResolverDef
import sweet.wong.gmark.core.log
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.repo.RepoViewModel
import java.io.File

class LinkResolver(private val viewModel: RepoViewModel) : LinkResolverDef() {

    override fun resolve(view: View, link: String) {
        if (link.startsWith("http")) {
            super.resolve(view, link)
            return
        }

        val currentFile = viewModel.currentFile ?: return toast("Current file is null")

        log("link is $link", "currentFile is $currentFile")

        val folder = currentFile.parentFile
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

}