package sweet.wong.gmark.filepreview.history

import java.io.File

data class HistoryFile(val file: File, val data: String, val scrollY: Int = 0)