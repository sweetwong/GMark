package sweet.wong.gmark.core

import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.math.min

private const val MIN_THREAD_COUNT = 3

private val executors = Executors.newFixedThreadPool(
    min(MIN_THREAD_COUNT, Runtime.getRuntime().availableProcessors() - 1)
)

fun io(action: () -> Unit) = executors.execute(action)

fun <T> io(task: () -> T): Future<T> = executors.submit(task)