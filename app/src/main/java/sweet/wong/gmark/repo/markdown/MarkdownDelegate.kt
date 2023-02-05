package sweet.wong.gmark.repo.markdown

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonReducer
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.recycler.MarkwonAdapter
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.Prism4jThemeDefault
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import io.noties.prism4j.annotations.PrismBundle
import org.commonmark.node.Node
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.repo.RepoViewModel
import sweet.wong.gmark.repo.markdown.plugins.ImagePlugin
import sweet.wong.gmark.repo.markdown.plugins.LinkPlugin
import sweet.wong.gmark.utils.Utils


@PrismBundle(include = ["java", "kotlin"], grammarLocatorClassName = ".GrammarLocatorSourceCode")
class MarkdownDelegate(repoViewModel: RepoViewModel) {

    private val prism4j = Prism4j(GrammarLocatorSourceCode())
    private val prism4jTheme =
        if (Utils.isDarkMode()) Prism4jThemeDarkula.create()
        else Prism4jThemeDefault.create()
    private val reducer = MarkwonReducer.directChildren()

    private var nodes: List<Node> = emptyList()

    private lateinit var markList: RecyclerView

    private val markwon: Markwon = Markwon.builder(App.app)
        .usePlugins(
            listOf(
                CoilImagesPlugin.create(App.app),
                HtmlPlugin.create(),
                TablePlugin.create(App.app),
                SyntaxHighlightPlugin.create(prism4j, prism4jTheme),
                ImagePlugin(),
                LinkPlugin(
                    getShowingFile = { repoViewModel.showingFile },
                    getNodes = { nodes },
                    onSelectFile = { repoViewModel.selectFile(it) },
                    onClickCatalog = { markList.smoothScrollToPosition(it) },
                    onClickUrl = { repoViewModel.selectUrl(it) }
                )
            )
        )
        .build()

    val adapter = MarkwonAdapter.builderTextViewIsRoot(R.layout.adapter_default_entry)
//        .include(
//            FencedCodeBlock::class.java,
//            SimpleEntry.create(R.layout.adapter_node_code_block, R.id.text_view)
//        )
        .build()


    @SuppressLint("NotifyDataSetChanged")
    fun setMarkdown(fileName: String, markList: RecyclerView, markdown: String) {
        // FIXME: 2021/9/7 这里会ANR
        this.markList = markList
        val template = if (fileName.endsWith(".md")) markdown
        else "```${getFileType(fileName)}\n$markdown\n```"
        markList.adapter = adapter
        val nodes = reducer.reduce(markwon.parse(template))
        adapter.setParsedMarkdown(markwon, nodes)
        adapter.notifyDataSetChanged()

        this.nodes = nodes
    }

    private fun getFileType(fileName: String): String {
        if (fileName.endsWith(".kt")) return "kotlin"
        if (fileName.endsWith(".java")) return "java"
        return ""
    }

}