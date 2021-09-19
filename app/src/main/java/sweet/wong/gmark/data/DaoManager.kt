package sweet.wong.gmark.data

import androidx.room.Room
import sweet.wong.gmark.core.App
import java.util.concurrent.ConcurrentHashMap

object DaoManager {

    val repoDao: RepoDao by lazy {
        Room.databaseBuilder(App.app, RepoDatabase::class.java, "repo")
            .allowMainThreadQueries()
            .build().repoDao()
    }

    private val pageDaoMap = ConcurrentHashMap<Repo, PageDao>()

    fun getPageDao(repo: Repo): PageDao {
        pageDaoMap[repo]?.let { return it }

        val pageDao = Room.databaseBuilder(
            App.app,
            PageDatabase::class.java,
            "page_" + repo.url.replace("/", "_")
        )
            .build()
            .pageDao()

        pageDaoMap[repo] = pageDao
        return pageDao
    }

}