package sweet.wong.gmark.debug

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.databinding.ActivityDebugBinding

class DebugActivity : BaseActivity<ActivityDebugBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.button.setOnClickListener {
            lifecycleScope.launch {
                repeat(101) { i ->
                    delay(10)
                    binding.progressBar.setProgress(i)
                    binding.tvProgress.text = i.toString()
                }
            }
        }
    }

}