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
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
) : Parcelable

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