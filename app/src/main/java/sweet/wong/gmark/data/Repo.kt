package sweet.wong.gmark.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Repo(
    @PrimaryKey @ColumnInfo(name = "url") var url: String,
    @ColumnInfo(name = "local_path") var localPath: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "username") var username: String?,
    @ColumnInfo(name = "password") var password: String?,
    @ColumnInfo(name = "ssh") var ssh: String?,
    @ColumnInfo(name = "time") var time: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "state") var state: Int = STATE_INIT,
) : Parcelable {

    companion object {
        const val STATE_INIT = 0
        const val STATE_SYNCING = 1
        const val STATE_FAILED = 2
        const val STATE_SUCCESS = 3
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Repo

        if (url != other.url) return false

        return true
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }

}

@Dao
interface RepoDao {

    @Query("SELECT * FROM repo WHERE url = :url")
    fun get(url: String): Repo?

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