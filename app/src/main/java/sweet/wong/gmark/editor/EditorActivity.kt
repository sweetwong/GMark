package sweet.wong.gmark.editor

import android.content.Context
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.databinding.ActivityEditorBinding
import sweet.wong.gmark.ext.start

class EditorActivity : BaseActivity<ActivityEditorBinding>() {

    companion object {

        fun start(context: Context) {
            context.start<EditorActivity>()
        }

    }

}