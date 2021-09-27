package sweet.wong.gmark.ext

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import sweet.wong.gmark.core.toast

val exceptionHandler = CoroutineExceptionHandler { _, e ->
    e.printStackTrace()
    toast(e)
}

val Dispatchers.IO_CATCH
    get() = Dispatchers.IO + exceptionHandler

val Dispatchers.MAIN_CATCH
    get() = Dispatchers.Main + exceptionHandler