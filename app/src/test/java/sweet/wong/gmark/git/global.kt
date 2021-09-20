package sweet.wong.gmark.git

import org.eclipse.jgit.api.Git
import java.io.File

const val rootPath = "D:\\Project\\Mine\\GMark"

val git: Git get() = Git.open(File(rootPath))

