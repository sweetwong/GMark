package sweet.wong.gmark.repo.git

import org.eclipse.jgit.diff.DiffEntry

sealed class DiffUIState {

    data class Success(val diffEntries: List<DiffEntry>) : DiffUIState()

    object Empty : DiffUIState()

    class Failed() : DiffUIState()

}

