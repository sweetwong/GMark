package sweet.wong.gmark.git

import org.eclipse.jgit.treewalk.filter.PathFilter
import org.junit.Test
import java.io.ByteArrayOutputStream

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
output: 
${getDiffOutput(newPath)}
                        
                    """
                )
            }
        }
    }

    private fun getDiffOutput(gitPath: String): String {
        val output = ByteArrayOutputStream()
        git.diff()
            .setPathFilter(PathFilter.create(gitPath))
            .setOutputStream(output)
            .call()
        return output.toString("UTF-8")
    }

}