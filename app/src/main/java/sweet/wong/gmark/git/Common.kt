package sweet.wong.gmark.git

import org.eclipse.jgit.api.Git
import sweet.wong.gmark.data.Repo
import java.io.File

object Common {

    const val TITLE_REMOTE_ENUMERATING_OBJECTS = "remote: Enumerating objects"
    const val TITLE_REMOTE_COUNTING_OBJECTS = "remote: Counting objects"
    const val TITLE_REMOTE_COMPRESSING_OBJECTS = "remote: Compressing objects"
    const val TITLE_RECEIVING_OBJECTS = "Receiving objects"
    const val TITLE_RESOLVING_DELTAS = "Resolving deltas"
    const val TITLE_UPDATING_REFERENCES = "Updating references"

}

val Repo.git: Git
    get() = Git.open(File(root))