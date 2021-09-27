package sweet.wong.gmark

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/26
 */
class FlowTest {

    @Test
    fun start() = runBlocking {
        fetch().collect {
            println(it)
        }
    }

    private fun fetch() = flow {
        emit(System.currentTimeMillis())
        delay(1000)
    }.flowOn(Dispatchers.Default)

}