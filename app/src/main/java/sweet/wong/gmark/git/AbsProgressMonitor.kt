package sweet.wong.gmark.git

import org.eclipse.jgit.lib.ProgressMonitor

abstract class AbsProgressMonitor : ProgressMonitor {

    override fun start(totalTasks: Int) {
    }

    override fun beginTask(title: String, totalWork: Int) {
    }

    override fun update(completed: Int) {
    }

    override fun endTask() {
    }

    override fun isCancelled(): Boolean {
        return false
    }

}