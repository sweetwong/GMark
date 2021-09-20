package sweet.wong.gmark.git

sealed class GitResult {

    object Success : GitResult()

    class Progress(val title: String?, val percent: Int) : GitResult()

    class Failure(val e: Exception) : GitResult()

}
