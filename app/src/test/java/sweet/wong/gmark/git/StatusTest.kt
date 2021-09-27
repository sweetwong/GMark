package sweet.wong.gmark.git

import org.junit.Test

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/27
 */
class StatusTest {

    @Test
    fun status() {
        val status = git.status().call()
        status.changed.forEach {
            println(it)
        }
        status.added.forEach {
            println(it)
        }
    }

}