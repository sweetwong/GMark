package sweet.wong.gmark.core

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import sweet.wong.gmark.utils.ThemeUtils

@SuppressLint("StaticFieldLeak")
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks by noOpDelegate() {
            override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
                ThemeUtils.setTheme(this@App)
                ThemeUtils.setTheme(activity)
            }
        })

//        UETool.showUETMenu()
    }

    companion object {
        lateinit var app: App
    }

}