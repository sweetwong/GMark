package sweet.wong.gmark.debug

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import sweet.wong.gmark.base.BaseActivity
import sweet.wong.gmark.databinding.ActivitySearchBinding

class SearchActivity : BaseActivity<ActivitySearchBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.etUrl.transitionName = SHARED_ELEMENT_NAME
    }

    companion object {

        private const val SHARED_ELEMENT_NAME = "search_edit_text"
        private const val EXTRA_URL = "extra_url"

        fun start(activity: Activity, view: View, url: String) {
            view.transitionName = SHARED_ELEMENT_NAME

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                view,
                SHARED_ELEMENT_NAME
            )

            val intent = Intent(activity, SearchActivity::class.java)
                .putExtra(EXTRA_URL, url)

            activity.startActivity(
                intent,
                options.toBundle()
            )
        }
    }

}