package sweet.wong.gmark.newrepo

object CloneUrlParser {

    fun parse(url: String): CloneUrlType {
        if (url.isBlank()) {
            return CloneUrlType.INVALID
        }

        if (url.startsWith("git@")) {
            return CloneUrlType.SSH
        }

        if (url.contains("https://github.com")) {
            return CloneUrlType.GITHUB
        }

        if (url.startsWith("http://")) {
            return CloneUrlType.HTTP
        }

        if (url.startsWith("https://")) {
            return CloneUrlType.HTTP
        }

        return CloneUrlType.INVALID
    }

}

enum class CloneUrlType {

    INVALID,
    GITHUB,
    SSH,
    HTTP

}