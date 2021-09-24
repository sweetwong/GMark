package sweet.wong.gmark.git

import org.junit.Test

class DiffTest {

    @Test
    fun diff() = timeCost {
        val diffList = git.diff().call()
        diffList.forEachIndexed { index, diffEntry ->
            diffEntry.apply {
                println(
                    """
                        index: $index
                        changeType: $changeType
                        newId: $newId
                        newMode: $newMode
                        newPath: $newPath
                        oldId: $oldId
                        oldMode: $oldMode
                        oldPath: $oldPath
                        score: $score
                        treeFilterMarks: $treeFilterMarks
                        
                    """.trimIndent()
                )
            }
        }
    }

}