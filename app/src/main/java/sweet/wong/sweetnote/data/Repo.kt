package sweet.wong.sweetnote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Repo(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "url") var url: String,
    @ColumnInfo(name = "local_path") var local_path: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "username") var username: String?,
    @ColumnInfo(name = "password") var password: String?,
    @ColumnInfo(name = "ssh") var ssh: String?
)