package sweet.wong.gmark.git

import org.junit.Test

class CheckoutTest {

    @Test
    fun revert() {
        revertFile("app/src/test/java/sweet/wong/gmark/git/AddTest1.kt")
    }

    private fun revertFile(path: String) {
        val git = git

        git.reset()
            .addPath(path)
            .call()

        git.clean()
            .setPaths(setOf(path))
            .setForce(true)
            .call()

        git.checkout()
            .addPath(path)
            .call()
    }

}