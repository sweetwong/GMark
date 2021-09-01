package sweet.wong.sweetnote.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Repo(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "local_path") val local_path: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "username") val username: String?,
    @ColumnInfo(name = "password") val password: String?,
    @ColumnInfo(name = "ssh") val ssh: String?
)