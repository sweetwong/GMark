package sweet.wong.gmark.git

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.eclipse.jgit.api.Git
import sweet.wong.gmark.data.Repo

object Clone {

    fun start(repo: Repo): Observable<Progress> = Observable.create<Progress> { emitter ->
        // Delete old files
        val rootFile = repo.rootFile
        if (rootFile.isDirectory) {
            rootFile.deleteRecursively()
        }

        // Start clone
        Git.cloneRepository()
            .setURI(repo.url)
            .setProgressMonitor(BasicProgressMonitor { progress -> emitter.onNext(progress) })
            .setDirectory(rootFile)
            .setCredential(repo)
            .call()

        emitter.onComplete()
    }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())

}