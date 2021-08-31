package sweet.wong.sweetnote

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        lateinit var app: App
    }

}