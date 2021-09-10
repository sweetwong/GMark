package sweet.wong.gmark.repo.markdown.plugins

import android.text.Layout
import android.text.style.AlignmentSpan
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.image.ImageProps
import org.commonmark.node.Image
import sweet.wong.gmark.photoview.PhotoViewActivity

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/7
 */
class GmarkImagePlugin : AbstractMarkwonPlugin() {

    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.appendFactory(Image::class.java) { configuration, props ->
            val url = ImageProps.DESTINATION.require(props)
            LinkSpan(configuration.theme(), url) { view, link ->
                view.transitionName = "GoodBoy"
                PhotoViewActivity.start(view.context, view, link)
            }
        }

        builder.appendFactory(Image::class.java) { _, _ ->
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER)
        }

    }

}