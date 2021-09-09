package sweet.wong.gmark.repo.history

import java.io.File

/**
 * Holding one RepoFragment's data
 */
data class Page(
    val file: File,
    val scrollY: Int = 0,
    val title: String? = null,
    val raw: String? = null
) {

    override fun equals(other: Any?): Boolean {
        return other == file
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }

}