package sweet.wong.gmark.repo.outline

data class Head(
    val title: String,
    val level: Int,
    val markdownPosition: Int,
    var visible: Boolean = true,
    var spinOpened: Boolean? = null
) {

    val titleWithBlank = calculateBlank()

    private fun calculateBlank(): String {
        val blank = StringBuilder()
        repeat(level - 1) {
            blank.append("  ")
        }
        return "$blank${title}"
    }

}