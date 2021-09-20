package sweet.wong.gmark.git

import org.junit.Test

class CommitTest {

    private val commitMessage = "[Feature] Support git test"

    @Test
    fun commit() {
        // Add all
        git.add()
            .addFilepattern(".")
            .call()

        // Commit
        git.commit()
            .setAuthor("sweetwong", "sweetwong@github.com")
            .setAll(true)
            .setMessage(commitMessage)
            .call()
    }

}