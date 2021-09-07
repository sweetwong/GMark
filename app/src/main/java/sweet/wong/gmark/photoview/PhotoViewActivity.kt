package sweet.wong.gmark.photoview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import coil.load
import sweet.wong.gmark.databinding.ActivityPhotoViewBinding

/**
 * @author sweetwang 2021/9/7
 */
class PhotoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra(EXTRA_URL)
        if (url.isNullOrEmpty()) {
            finish()
            return
        }

        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.photoImage.setOnClickListener {
            ActivityCompat.finishAfterTransition(this)
        }

        binding.photoImage.load(url)
    }

    companion object {

        private const val EXTRA_URL = "extra_url"

        fun start(context: Context, view: View, url: String) {
            if (context !is Activity) return

            val options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(context, view, "GoodBoy")

            context.startActivity(
                Intent(context, PhotoViewActivity::class.java).putExtra(EXTRA_URL, url), options.toBundle()
            )
        }
    }

}