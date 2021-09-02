package sweet.wong.sweetnote.data

import androidx.room.*

@Dao
interface RepoDao {
    @Query("SELECT * FROM repo")
    fun getAll(): List<Repo>

    @Insert
    fun insertAll(vararg repos: Repo)

    @Delete
    fun delete(repo: Repo)

    @Update
    fun update(repo: Repo)

}