package sweet.wong.sweetnote.filepreview.markdown

import android.view.View
import io.noties.markwon.LinkResolverDef
import sweet.wong.sweetnote.core.log
import sweet.wong.sweetnote.core.toast
import sweet.wong.sweetnote.filepreview.FilePreviewViewModel
import java.io.File

class LinkResolver(private val viewModel: FilePreviewViewModel) : LinkResolverDef() {

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