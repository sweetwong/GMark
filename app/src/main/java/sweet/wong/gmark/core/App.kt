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
                App.activity = activity
                ThemeUtils.setTheme(activity)
            }

            override fun onActivityResumed(activity: Activity) {
                App.activity = activity
            }
        })
    }

    companion object {

        lateinit var app: App
        lateinit var activity: Activity
    }

}