package sweet.wong.gmark.repo.drawer.project

import java.io.File

data class ProjectUIState(
    val drawerFile: File,
    val currentFile: File,
    val rootFile: File,
    val navigateBack: Boolean = false
) {

    constructor(uiState: ProjectUIState, file: File) : this(
        file,
        uiState.currentFile,
        uiState.rootFile,
        uiState.navigateBack
    )

}