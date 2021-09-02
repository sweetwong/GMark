package sweet.wong.sweetnote.repodetail.drawer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.repodetail.RepoViewerViewModel

class DrawerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val activity: AppCompatActivity = context as AppCompatActivity
    private val viewModel: RepoViewerViewModel by activity.viewModels()

    private val recyclerView: RecyclerView
    private val adapter: DrawerProjectAdapter

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_drawer, this)
        recyclerView = findViewById(R.id.recycler_view)

        adapter = DrawerProjectAdapter(viewModel)
        recyclerView.adapter = adapter

        viewModel.projectChildFiles.observe(activity) {
            adapter.submitList(it.toMutableList())
        }
    }

    fun onBackPressed() = adapter.onBackPressed()

}