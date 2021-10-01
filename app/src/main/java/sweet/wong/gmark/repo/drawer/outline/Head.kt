package sweet.wong.gmark.repo.drawer.outline

data class Head(
    val title: String,
    val level: Int,
    val markdownPosition: Int,
    var visible: Boolean = level == 1,
    var spinOpened: Boolean? = null
)