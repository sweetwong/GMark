package sweet.wong.gmark.data

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
@Entity
data class Page(
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "scrollY") var scrollY: Int = 0,
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    val file = File(path)

}

@Dao
interface PageDao {

    @Query("SELECT * FROM page WHERE uid = :uid")
    fun get(uid: Int): Page

    @Query("SELECT * FROM page")
    fun getAll(): List<Page>

    @Query("DELETE FROM page WHERE path = :path")
    fun deleteByPath(path: String)

    @Insert
    fun insertAll(vararg pages: Page)

    @Delete
    fun delete(page: Page)

    @Update
    fun update(page: Page)

}

@Database(entities = [Page::class], version = 2, exportSchema = false)
abstract class PageDatabase : RoomDatabase() {

    abstract fun pageDao(): PageDao

}