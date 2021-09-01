package sweet.wong.sweetnote.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RepoDao {
    @Query("SELECT * FROM repo")
    fun getAll(): List<Repo>

    @Insert
    fun insertAll(vararg users: Repo)

    @Delete
    fun delete(user: Repo)
}