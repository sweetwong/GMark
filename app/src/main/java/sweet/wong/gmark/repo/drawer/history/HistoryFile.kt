package sweet.wong.gmark.repo.drawer.history

import java.io.File

data class HistoryFile(val file: File, val data: String, val scrollY: Int = 0)