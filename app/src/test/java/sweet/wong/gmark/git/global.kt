package sweet.wong.gmark.git

import org.eclipse.jgit.api.Git
import java.io.File

val root = File(System.getProperty("user.dir"), "..")

val git: Git
    get() = Git.open(root)

inline fun timeCost(action: () -> Unit) {
    val start = System.currentTimeMillis()
    action()
    println("Cost ${System.currentTimeMillis() - start}ms")
}