package sweet.wong.gmark.repo

import java.io.File

/**
 * Hold file raw text
 *
 * @author sweetwang 2021/9/7
 */
data class FileRaw(val file: File, val raw: String)