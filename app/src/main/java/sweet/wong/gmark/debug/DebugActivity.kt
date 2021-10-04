package sweet.wong.gmark.debug

import android.os.Bundle
import android.view.Menu
import sweet.wong.gmark.R
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.databinding.ActivityDebugBinding

class DebugActivity : BaseActivity<ActivityDebugBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.initView()
    }

    private fun ActivityDebugBinding.initView() {
        setSupportActionBar(toolbar)
        layoutInflater.inflate(R.layout.pref_editor_dialog, binding.toolbar, true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_debug, menu)
        return true
    }

}