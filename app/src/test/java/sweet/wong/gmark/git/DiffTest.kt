package sweet.wong.gmark.git

import org.junit.Test

class DiffTest {

    // TODO: 2021/9/27 Support diff formatter
    @Test
    fun diff() = timeCost {
        val diffList = git.diff().call()
//        val diffOutput = ByteArrayOutputStream()
//        val diffFormatter = DiffFormatter(diffOutput)
//        diffFormatter.setRepository(git.repository)
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