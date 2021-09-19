package sweet.wong.gmark.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Repo(
    @ColumnInfo(name = "url") var url: String,
    @ColumnInfo(name = "local_path") var localPath: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "username") var username: String?,
    @ColumnInfo(name = "password") var password: String?,
    @ColumnInfo(name = "ssh") var ssh: String?,
    @ColumnInfo(name = "lastUpdateTime") var lastUpdateTime: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "state") var state: Int = STATE_INIT,
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
) : Parcelable {

    companion object {
        const val STATE_INIT = 0
        const val STATE_SYNCING = 1
        const val STATE_FAILED = 2
        const val STATE_SUCCESS = 3
    }
}

@Dao
interface RepoDao {

    @Query("SELECT * FROM repo WHERE uid = :uid")
    fun get(uid: Int): Repo

    @Query("SELECT * FROM repo")
    fun getAll(): List<Repo>

    @Insert
    fun insertAll(vararg repos: Repo)

    @Delete
    fun delete(repo: Repo)

    @Update
    fun update(repo: Repo)
}

@Database(entities = [Repo::class], version = 1, exportSchema = false)
abstract class RepoDatabase : RoomDatabase() {

    abstract fun repoDao(): RepoDao

}