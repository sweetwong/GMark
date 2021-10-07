package sweet.wong.gmark.repo.drawer.project

import sweet.wong.gmark.R
import sweet.wong.gmark.utils.UIState
import java.io.File

/**
 * Binding layout [R.layout.recycle_item_project]
 *
 * @param drawerFile Current file item
 * @param showingFile Current showing markdown file
 * @param rootFile Repository local path
 */
data class ProjectUIState(
    val drawerFile: File,
    val showingFile: File,
    val rootFile: File,
    val isNavigateBack: Boolean = false,
    var isEditing: Boolean = false
) : UIState<ProjectUIState>() {

    var name: String = if (isNavigateBack) ".." else drawerFile.name

    var editingText: String = name

    var isHighlight: Boolean = false

    constructor(uiState: ProjectUIState, file: File) : this(
        file,
        uiState.showingFile,
        uiState.rootFile,
        uiState.isNavigateBack,
        uiState.isEditing
    )

}