package sweet.wong.gmark.git

import org.eclipse.jgit.api.Git
import java.io.File

const val rootPath = "D:\\Project\\Mine\\GMark"

val git: Git get() = Git.open(File(rootPath))

inline fun timeCost(action: () -> Unit) {
    val start = System.currentTimeMillis()
    action()
    println("Cost ${System.currentTimeMillis() - start}ms")
}