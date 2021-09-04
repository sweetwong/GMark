package sweet.wong.gmark.ext

import android.util.TypedValue
import sweet.wong.gmark.core.App

val Int.dp: Int
    get() = toFloat().dp

val Float.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        App.app.resources.displayMetrics
    ).toInt()