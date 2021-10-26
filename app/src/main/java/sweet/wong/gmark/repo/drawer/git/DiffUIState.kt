package sweet.wong.gmark.repo.drawer.git

import org.eclipse.jgit.diff.DiffEntry
import sweet.wong.gmark.utils.UIState

data class DiffUIState(val entry: DiffEntry) : UIState<DiffUIState>() {

    val path: String
        get() =
            if (entry.changeType == DiffEntry.ChangeType.DELETE) entry.oldPath
            else entry.newPath

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DiffUIState

        if (path != other.path) return false

        return true
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }


}

