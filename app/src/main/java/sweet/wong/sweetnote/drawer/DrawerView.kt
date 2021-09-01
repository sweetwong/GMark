package sweet.wong.sweetnote.drawer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.PathUtils
import sweet.wong.sweetnote.R

class DrawerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val recyclerView: RecyclerView
    private val adapter: ProjectAdapter

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_drawer, this)
        recyclerView = findViewById(R.id.recycler_view)

        adapter = ProjectAdapter()
        recyclerView.adapter = adapter
        adapter.refresh(PathUtils.getExternalAppFilesPath() + "/QA")
    }

    fun onBackPressed() {
        adapter.back()
    }


}