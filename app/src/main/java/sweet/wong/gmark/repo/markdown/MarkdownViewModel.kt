package sweet.wong.gmark.repo.markdown

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.repo.outline.Head

class MarkdownViewModel : ViewModel() {

    val nodesToAllHeads = MutableLiveData<List<Node>>().apply {
        // Map nodes to heads
        observeForever { nodes ->
            val newHeads = mutableListOf<Head>()
            repeat(nodes.size) { i ->
                val node = nodes[i]
                if (node is Heading) {
                    val title = (node.firstChild as? Text)?.literal.orEmpty()
                    newHeads.add(Head(title, node.level, i))
                }
            }
            initSpinner(newHeads)
            this@MarkdownViewModel.allHeadsToShowingHead.value = newHeads
        }
    }

    /**
     * If head is invisible, don't add to list
     */
    private val allHeadsToShowingHead = MutableLiveData<MutableList<Head>>().apply {
        observeForever { allHeads ->
            this@MarkdownViewModel.showingHeads.value = mutableListOf<Head>().apply {
                allHeads.forEach { if (it.visible) add(it) }
            }
        }
    }

    val showingHeads = MutableLiveData<MutableList<Head>>()

    /**
     * Check if a head has child, if has, then show spinner
     */
    private fun initSpinner(allHeads: MutableList<Head>) {
        for (i in 0 until allHeads.size - 1) {
            val head = allHeads[i]
            if (head.level < allHeads[i + 1].level) {
                allHeads[i].spinOpened = false
            }
        }
    }

    /**
     * Trigger when spinner is clicked
     */
    fun selectSpinner(head: Head) {
        // This is not gonna happened, cause if we choose spinner, spinOpened must not be null
        head.spinOpened ?: return

        val allHeads =
            this.allHeadsToShowingHead.value ?: return toast("Select spinner heads is empty")

        // Find position
        var position = -1
        repeat(allHeads.size) { i ->
            if (allHeads[i].markdownPosition == head.markdownPosition) {
                position = i
            }
        }
        if (position == -1) return toast("Select spinner invalid position")

        // Traversal children
        var i = position + 1
        while (i < allHeads.size) {
            val child = allHeads[i]

            // When great level was found, break
            if (child.level <= head.level) {
                break
            }

            // Tell diff util this object has changed
            allHeads[i] = child.copy(
                // If don't has spinner, just keep null
                // If has spinner, close it
                spinOpened = if (child.spinOpened == null) null else false,
                // If is next level, check spinner state
                // If is not next level, set invisible
                visible = if (child.level - head.level != 1) false else head.spinOpened == true
            )
            i++
        }

        // Refresh children
        this.allHeadsToShowingHead.value = allHeads
    }

}