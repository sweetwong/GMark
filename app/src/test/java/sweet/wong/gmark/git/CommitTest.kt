package sweet.wong.gmark.git

import org.junit.Test

class CommitTest {

    @Test
    fun commit() {
        git.commit()
            .setAuthor("sweetwong", "sweetwong@github.com")
            .setAll(true)
            .setMessage("commit test")
            .call()
    }

}