package sweet.wong.gmark.git

import org.eclipse.jgit.api.Git
import java.io.File

val git: Git
    get() = Git.open(File(System.getProperty("user.dir"), ".."))

inline fun timeCost(action: () -> Unit) {
    val start = System.currentTimeMillis()
    action()
    println("Cost ${System.currentTimeMillis() - start}ms")
}