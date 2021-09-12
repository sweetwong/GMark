package sweet.wong.gmark.repo.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.commonmark.node.Heading
import org.commonmark.node.Node
import org.commonmark.node.Text
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.repo.outline.Head

class MarkdownViewModel : ViewModel() {

    val nodes = MutableLiveData<List<Node>>().apply {
        // Map nodes to heads
        observeForever { nodes ->
            val newHeads = mutableListOf<Head>()
            repeat(nodes.size) { i ->
                val node = nodes[i]
                if (node is Heading) {
                    val title = (node.firstChild as Text).literal
                    newHeads.add(Head(title, node.level, i))
                }
            }
            initSpin(newHeads)
            this@MarkdownViewModel.allHeads.value = newHeads
        }
    }

    private val allHeads = MutableLiveData<MutableList<Head>>().apply {
        observeForever { allHeads ->
            val showingHeads = mutableListOf<Head>()
            allHeads.forEach {
                if (it.visible) {
                    showingHeads.add(it)
                }
            }
            this@MarkdownViewModel.showingHeads.value = showingHeads
        }
    }

    val showingHeads = MutableLiveData<MutableList<Head>>()

    private fun initSpin(allHeads: MutableList<Head>) {
        for (i in 0 until allHeads.size - 1) {
            val head = allHeads[i]
            if (head.level < allHeads[i + 1].level) {
                allHeads[i].spinOpened = true
            }
        }
    }

    fun selectSpinner(head: Head) {
        if (head.spinOpened == null) return

        val allHeads = this.allHeads.value ?: return toast("Select spinner heads is empty")

        // Find position
        var position = -1
        repeat(allHeads.size) { i ->
            if (allHeads[i].markdownPosition == head.markdownPosition) {
                position = i
            }
        }

        if (position == -1) return toast("Select spinner invalid position")

        var i = position + 1
        while (i < allHeads.size) {
            val child = allHeads[i]
            if (child.level <= head.level) {
                break
            }
            // Tell diff util this object has changed
            allHeads[i] = child.copy(visible = !child.visible)
            i++
        }

        this.allHeads.value = allHeads
    }

}