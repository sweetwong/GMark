package sweet.wong.gmark.repo.drawer.git

import org.eclipse.jgit.diff.DiffEntry
import sweet.wong.gmark.utils.UIState

data class DiffUIState(val entry: DiffEntry) : UIState()

