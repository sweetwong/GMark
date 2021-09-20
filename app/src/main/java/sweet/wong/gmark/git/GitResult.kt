package sweet.wong.gmark.git

sealed class GitResult {

    object Success : GitResult()

    data class Progress(val title: String?, val percent: Int) : GitResult()

    data class Failure(val e: Exception) : GitResult()

}
