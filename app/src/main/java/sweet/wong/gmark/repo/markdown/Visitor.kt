package sweet.wong.gmark.repo.markdown

import io.noties.markwon.MarkwonVisitor
import org.commonmark.node.Heading

class Visitor : MarkwonVisitor.NodeVisitor<Heading> {

    override fun visit(visitor: MarkwonVisitor, heading: Heading) {
    }

}