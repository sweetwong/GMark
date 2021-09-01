package sweet.wong.sweetnote.utils

import com.yanzhenjie.permission.AndPermission
import sweet.wong.sweetnote.core.App
import sweet.wong.sweetnote.core.toast

object PermissionUtils {

    fun onGranted(groups: Array<String>, onGranted: () -> Unit) {
        AndPermission.with(App.app)
            .runtime()
            .permission(groups)
            .onGranted {
                onGranted()
            }
            .onDenied {
                toast("Permissions are denied")
            }
            .start()
    }

}