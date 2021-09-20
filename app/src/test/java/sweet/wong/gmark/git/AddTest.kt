package sweet.wong.gmark.git

import org.junit.Test

class AddTest {

    @Test
    fun add() {
        git.add()
            .addFilepattern(".")
            .call()
    }

}