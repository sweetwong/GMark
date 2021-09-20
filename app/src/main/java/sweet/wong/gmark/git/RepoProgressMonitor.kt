package sweet.wong.gmark.git

import kotlinx.coroutines.channels.ProducerScope
import org.eclipse.jgit.lib.ProgressMonitor
import sweet.wong.gmark.core.log
import sweet.wong.gmark.git.Common.TITLE_UPDATING_REFERENCES

class RepoProgressMonitor(private val scope: ProducerScope<GitResult>) : ProgressMonitor {

    private var mTotalWork = 0
    private var mWorkDone = 0
    private var mLastProgress = 0
    private var mTitle: String? = null

    private fun publishProgressInner() {
        if (mTotalWork != 0) {
            val p = 100 * mWorkDone / mTotalWork
            if (p - mLastProgress < 1) {
                return
            }
            mLastProgress = p
        }

        log("Git monitor onProgress", "title", mTitle, "percent", mLastProgress)
        scope.trySend(GitResult.Progress(mTitle, mLastProgress))
        if (mTitle == TITLE_UPDATING_REFERENCES && mLastProgress == 100) {
            scope.trySend(GitResult.Success)
        }
    }

    override fun start(totalTasks: Int) {
        publishProgressInner()
    }

    override fun beginTask(title: String?, totalWork: Int) {
        mTotalWork = totalWork
        mWorkDone = 0
        mLastProgress = 0
        mTitle = title
        publishProgressInner()
    }

    override fun update(i: Int) {
        mWorkDone += i
        publishProgressInner()
    }

    override fun endTask() {}

    override fun isCancelled(): Boolean {
        return false
    }
}
