package sweet.wong.gmark.git

import org.eclipse.jgit.storage.file.FileBasedConfig
import org.junit.Test

class ConfigTest {

    @Test
    fun config() {
        val config = git.repository.config
        println((config as FileBasedConfig).file.readText())
    }

}