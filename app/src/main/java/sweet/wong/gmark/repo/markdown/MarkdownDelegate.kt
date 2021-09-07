package sweet.wong.gmark.repo.markdown

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.recycler.SimpleEntry
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
import org.commonmark.node.FencedCodeBlock
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.repo.RepoViewModel


@PrismBundle(include = ["java", "kotlin"], grammarLocatorClassName = ".GrammarLocatorSourceCode")
class MarkdownDelegate(viewModel: RepoViewModel) {

    private val prism4j = Prism4j(GrammarLocatorSourceCode())
    private val prism4jTheme = Prism4jThemeDefault.create(Color.WHITE)

    private val markwon: Markwon = Markwon.builder(App.app)
        .usePlugins(
            listOf(
                CoilImagesPlugin.create(App.app),
                HtmlPlugin.create(),
                TablePlugin.create(App.app),
                SyntaxHighlightPlugin.create(prism4j, prism4jTheme),
                GmarkImagePlugin(),
                LinkPlugin(viewModel)
            )
        )
        .build()

    val adapter = MarkwonAdapter.builderTextViewIsRoot(R.layout.adapter_default_entry)
        .include(
            FencedCodeBlock::class.java,
            SimpleEntry.create(R.layout.adapter_node_code_block, R.id.text_view)
        )
        .build()


    fun setMarkdown(fileName: String, markList: RecyclerView, markdown: String) {
        val template = if (fileName.endsWith(".md")) markdown
        else "```${getFileType(fileName)}\n$markdown\n```"
        markList.adapter = adapter
        val node = markwon.parse(template)
        adapter.setParsedMarkdown(markwon, node)
        adapter.notifyDataSetChanged()
    }

    private fun getFileType(fileName: String): String {
        if (fileName.endsWith(".kt")) return "kotlin"
        if (fileName.endsWith(".java")) return "java"
        return ""
    }

}