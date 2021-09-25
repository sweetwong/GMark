package sweet.wong.gmark.repo.project

import sweet.wong.gmark.R
import sweet.wong.gmark.utils.UIState
import java.io.File

/**
 * Binding layout [R.layout.recycle_item_project]
 */
data class ProjectUIState(
    val drawerFile: File,
    val showingFile: File,
    val rootFile: File,
    val isNavigateBack: Boolean = false,
    var editing: Boolean = false
) : UIState() {

    var name: String = if (isNavigateBack) ".." else drawerFile.name

    constructor(uiState: ProjectUIState, file: File) : this(
        file,
        uiState.showingFile,
        uiState.rootFile,
        uiState.isNavigateBack,
        uiState.editing
    )

}