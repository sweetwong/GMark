package sweet.wong.gmark.repolist

import org.eclipse.jgit.lib.ProgressMonitor
import sweet.wong.gmark.core.Event
import java.util.*

/**
 */
class RepoCloneMonitor(
    private val viewModel: RepoListViewModel,
    private val uiState: RepoUIState
) : ProgressMonitor {

    private var mTotalWork = 0
    private var mWorkDone = 0
    private var mLastProgress = 0
    private var mTitle: String? = null

    private fun publishProgressInner() {
        var status = ""
        var percent = ""
        if (mTitle != null) {
            status = String.format(Locale.getDefault(), "%s ... ", mTitle)
            percent = "0%"
        }
        if (mTotalWork != 0) {
            val p = 100 * mWorkDone / mTotalWork
            if (p - mLastProgress < 1) {
                return
            }
            mLastProgress = p
            percent = String.format(Locale.getDefault(), "(%d%%)", p)
        }

        uiState.statusText = status + percent
        uiState.progress = percent
        viewModel.repoUpdateEvent.postValue(Event(uiState.position))
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
//        return isTaskCanceled()
    }
}
