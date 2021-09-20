package sweet.wong.gmark.git

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test
import sweet.wong.gmark.data.Repo
import java.io.File

class CloneTest {

    private val localPath = "D:\\Project\\Mine\\GMark\\gittest"

    @Test
    fun delete() {
        // Delete if exists
        val file = File(localPath)
        if (file.exists() && file.isDirectory) {
            file.deleteRecursively()
        }
    }

    @Test
    fun clone(): Unit = runBlocking {
        // Start clone
        val repo = Repo(
            url = "https://github.com/sweetwong/GMark.git",
            localPath = localPath,
            name = "Gmark",
            username = null,
            password = null,
            ssh = null
        )

        Clone.start(repo).collect {
            println(it)
        }
    }

}