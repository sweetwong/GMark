package sweet.wong.gmark.data

import androidx.room.Room
import sweet.wong.gmark.core.App

object DaoManager {

    val repoDao: RepoDao
        get() = Room.databaseBuilder(App.app, RepoDatabase::class.java, "repo").build().repoDao()

}