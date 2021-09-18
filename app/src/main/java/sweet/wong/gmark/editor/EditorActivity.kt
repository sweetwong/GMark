package sweet.wong.gmark.editor

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import sweet.wong.gmark.R
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.ActivityEditorBinding
import sweet.wong.gmark.ext.start
import java.io.File

class EditorActivity : BaseActivity<ActivityEditorBinding>() {

    private val viewModel: EditorViewModel by viewModels()
    private lateinit var undoRedo: TextViewUndoRedo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val path = intent.getStringExtra(EXTRA_PATH)
        if (path == null) {
            toast("Path is null")
            finish()
            return
        }
        val file = File(path)
        if (!file.exists() || !file.isFile) {
            toast("File doesn't not exist")
            finish()
            return
        }
        viewModel.file.value = file

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        undoRedo = TextViewUndoRedo(binding.etRaw)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_editor, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_confirm -> {
                toast("不错")
                return true
            }
            R.id.menu_undo -> {
                undoRedo.undo()
                return true
            }
            R.id.menu_redo -> {
                undoRedo.redo()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        private const val EXTRA_PATH = "extra_path"

        fun start(context: Context, path: String) {
            context.start<EditorActivity> {
                putExtra(EXTRA_PATH, path)
            }
        }

    }

}