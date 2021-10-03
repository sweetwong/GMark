package sweet.wong.gmark.data

import android.os.Parcelable
import androidx.room.*
import com.blankj.utilcode.util.RegexUtils
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.*

@Parcelize
@Entity
data class Page(
    @ColumnInfo(name = "path") val path: String,
    @ColumnInfo(name = "scrollY") var scrollY: Int = 0,
    @ColumnInfo(name = "name") var name: String? = null,
    @PrimaryKey val uid: String = UUID.randomUUID().toString()
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    val pageType: PageType = run {
        if (RegexUtils.isURL(path)) {
            PageType.URL
        } else {
            PageType.FILE
        }
    }

    @IgnoredOnParcel
    @Ignore
    val file: File? = if (pageType == PageType.FILE) File(path) else null

}

enum class PageType {

    FILE,
    URL

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