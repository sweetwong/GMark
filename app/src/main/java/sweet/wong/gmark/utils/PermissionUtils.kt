package sweet.wong.gmark.utils

import com.yanzhenjie.permission.AndPermission
import sweet.wong.gmark.core.App
import sweet.wong.gmark.core.toast

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