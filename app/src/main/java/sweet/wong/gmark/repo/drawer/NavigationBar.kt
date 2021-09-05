package sweet.wong.gmark.repo.drawer

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import java.io.File

class NavigationBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    var onItemClick: ((File) -> Unit)?
        set(value) { adapter.onItemClick = value }
        get() = adapter.onItemClick

    private val adapter = NavigationBarAdapter()

    init {
        layoutManager = FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP)
        setAdapter(adapter)
        adapter.onItemClick = onItemClick
    }

    fun updateFile(file: File, root: File) {
        val list = mutableListOf<File>()
        var current: File? = file
        while (current != root && current != null) {
            list.add(current)
            current = current.parentFile
        }
        adapter.submitList(list.reversed())
    }

}