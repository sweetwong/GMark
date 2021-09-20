package sweet.wong.gmark.git

import org.junit.Test

class DiffTest {

    @Test
    fun diff() {
        val diffList = git.diff().call()

        println(diffList)
    }

}