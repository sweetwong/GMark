package sweet.wong.gmark.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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