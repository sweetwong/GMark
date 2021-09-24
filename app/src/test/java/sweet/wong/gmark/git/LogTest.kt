package sweet.wong.gmark.git

import com.blankj.utilcode.util.TimeUtils
import org.junit.Test

class LogTest {

    @Test
    fun log() = timeCost {
        val revCommits = git.log().call()
        revCommits.forEachIndexed { index, revCommit ->
            revCommit.apply {
                println(
                    """
                        index: $index
                        name: $name
                        id: $id
                        type: $type
                        firstByte: $firstByte
                        authorIdent: $authorIdent
                        commitTime: $commitTime, ${TimeUtils.millis2String((commitTime * 1000L))}
                        committerIdent: $committerIdent
                        encoding: $encoding
                        encodingName: $encodingName
                        footerLines: $footerLines
                        fullMessage: ${fullMessage.trimIndent()}
                        parentCount: $parentCount
                        parents: $parents
                        rawBuffer: $rawBuffer
                        shortMessage: $shortMessage
                        tree: $tree
                        
                    """.trimIndent()
                )
            }
        }
    }

}