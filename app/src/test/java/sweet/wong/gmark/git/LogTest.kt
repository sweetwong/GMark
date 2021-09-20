package sweet.wong.gmark.git

import org.junit.Test

class LogTest {

    @Test
    fun log() {
        val revCommits = git.log().call()
        revCommits.forEachIndexed { index, revCommit ->
            println(revCommit.shortMessage)
            println()
        }
    }

}