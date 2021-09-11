package sweet.wong.gmark.data

import androidx.room.Room
import sweet.wong.gmark.core.App

object DaoManager {

    val repoDao: RepoDao
        get() = Room.databaseBuilder(App.app, RepoDatabase::class.java, "repo")
            .allowMainThreadQueries()
            .build().repoDao()

    fun getPageDao(repo: Repo) =
        Room.databaseBuilder(App.app, PageDatabase::class.java, "page_" + repo.uid)
            .build()
            .pageDao()

}