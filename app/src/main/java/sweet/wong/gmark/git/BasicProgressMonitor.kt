package sweet.wong.gmark.git

import org.eclipse.jgit.lib.ProgressMonitor
import kotlin.math.min

class BasicProgressMonitor(private val onProgress: (Progress) -> Unit) : AbsProgressMonitor() {

    private var mTotalWork = 0
    private var mWorkDone = 0
    private var mLastProgress = 0
    private var mTitle: String? = null

    override fun beginTask(title: String, totalWork: Int) {
        mTotalWork = totalWork
        mWorkDone = 0
        mLastProgress = 0
        mTitle = title
        setProgress()
    }

    override fun update(completed: Int) {
        mWorkDone += completed
        if (mTotalWork != ProgressMonitor.UNKNOWN && mTotalWork != 0 && mTotalWork - mLastProgress >= 1) {
            setProgress()
            mLastProgress = mWorkDone
        }
    }

    private fun setProgress() {
        val message = mTitle
        val showedWorkDown = min(mWorkDone, mTotalWork)
        var progress = 0
        var rightHint = "0/0"
        var leftHint = "0%"
        if (mTotalWork != 0) {
            progress = 100 * showedWorkDown / mTotalWork
            leftHint = "$progress%"
            rightHint = "$showedWorkDown/$mTotalWork"
        }
        onProgress(Progress(message, leftHint, rightHint, progress))
    }

}

data class Progress(
    val message: String?,
    val leftHint: String?,
    val rightHint: String?,
    val progress: Int
) {
    val combinedMessage: String
        get() = "$message: $leftHint ($rightHint)"
}