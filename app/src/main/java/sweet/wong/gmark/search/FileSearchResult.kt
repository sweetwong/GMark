package sweet.wong.gmark.search

import java.io.File

class FileSearchResult(
    val file: File,
    val relativePath: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileSearchResult

        if (file != other.file) return false

        return true
    }

    override fun hashCode(): Int {
        return file.hashCode()
    }

}