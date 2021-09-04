package sweet.wong.gmark.data

import androidx.room.Room
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import sweet.wong.gmark.core.App

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/2
 */
object RepoModel {

    private val repoDao: RepoDao
        get() {
            val db = Room.databaseBuilder(App.app, RepoDatabase::class.java, "repo").build()
            return db.repoDao()
        }

    fun getAll(): Observable<List<Repo>> = Observable.fromCallable { repoDao.getAll() }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun insertAll(vararg repos: Repo): Observable<Unit> =
        Observable.fromCallable { repoDao.insertAll(*repos) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

    fun delete(repo: Repo): Observable<Unit> = Observable.fromCallable { repoDao.delete(repo) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

    fun update(repo: Repo): Observable<Unit> = Observable.fromCallable { repoDao.update(repo) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

}