package sweet.wong.gmark.ext

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner

val View.lifecycleOwner: LifecycleOwner
    get() = (context as AppCompatActivity)