package sweet.wong.gmark.git

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test
import sweet.wong.gmark.data.Repo
import java.io.File

class CloneTest {

    private val root = "D:\\Project\\Mine\\GMark\\gittest"

    @Test
    fun delete() {
        // Delete if exists
        val file = File(root)
        if (file.exists() && file.isDirectory) {
            file.deleteRecursively()
        }
    }

    @Test
    fun clone(): Unit = runBlocking {
        // Start clone
        val repo = Repo(
            url = "https://github.com/sweetwong/GMark.git",
            root = root,
            name = "Gmark",
            username = null,
            password = null,
            ssh = null
        )

        Clone.start(repo).collect {
            println(it)
        }
    }

    @Test
    fun gitHubClone(): Unit = runBlocking {
        // Start clone
        val repo = Repo(
            url = "https://github.com/sweetwong/Android-Interview-QA.git",
            root = root,
            name = "Android-Interview-QA",
            username = "Add token here",
            password = "",
            ssh = null
        )

        Clone.start(repo).collect {
            println(it)
        }
    }

}