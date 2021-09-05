package sweet.wong.gmark.core

import android.os.Handler
import android.os.Looper
import androidx.annotation.NonNull
import androidx.annotation.VisibleForTesting
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Global executor pools for the whole application.
 *
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
class AppExecutors @VisibleForTesting internal constructor(
    private val diskIO: Executor,
    private val networkIO: Executor,
    private val mainThread: Executor
) {
    constructor() : this(
        DiskIOThreadExecutor(),
        Executors.newFixedThreadPool(THREAD_COUNT),
        MainThreadExecutor()
    )

    fun diskIO(): Executor {
        return diskIO
    }

    fun networkIO(): Executor {
        return networkIO
    }

    fun mainThread(): Executor {
        return mainThread
    }

    /**
     * Executor that runs a task on a new background thread.
     */
    class DiskIOThreadExecutor : Executor {

        private val mDiskIO: Executor

        override fun execute(@NonNull command: Runnable) {
            mDiskIO.execute(command)
        }

        init {
            mDiskIO = Executors.newSingleThreadExecutor()
        }

    }

    private class MainThreadExecutor : Executor {

        private val mainThreadHandler = Handler(Looper.getMainLooper())

        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }

    }

    companion object {
        private const val THREAD_COUNT = 3
    }

}