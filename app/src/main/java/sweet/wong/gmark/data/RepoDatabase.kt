package sweet.wong.gmark.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Repo::class], version = 1)
abstract class RepoDatabase : RoomDatabase() {

    abstract fun repoDao(): RepoDao

}