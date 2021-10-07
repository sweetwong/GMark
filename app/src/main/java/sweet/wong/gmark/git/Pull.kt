package sweet.wong.gmark.git

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import sweet.wong.gmark.data.Repo

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/7
 */
object Pull {

    fun start(repo: Repo): Observable<Progress> = Observable.create<Progress> { emitter ->
        val git = repo.git
        val config = git.repository.config
        val remotes: Set<String> = config.getSubsections("remote")
        val remote = remotes.first()
        git.pull()
            .setProgressMonitor(BasicProgressMonitor { progress -> emitter.onNext(progress) })
            .setRemote(remote)
            .setCredential(repo)
            .call()
        emitter.onComplete()
    }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

}