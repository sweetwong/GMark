package sweet.wong.sweetnote.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.greenrobot.eventbus.EventBus
import sweet.wong.sweetnote.R
import sweet.wong.sweetnote.event.TextUpdateEvent
import java.io.File

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/1
 */
class ProjectAdapter : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    private var currentPath = ""

    private var files = emptyList<File>()

    fun refresh(path: String) {
        currentPath = path
        files = File(currentPath).listFiles().toList()
        notifyDataSetChanged()
    }

    fun back() {
        refresh(File(currentPath).parent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_item_project, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder) {
        text.text = files[position].name
    }

    override fun getItemCount(): Int = files.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val text: TextView = itemView.findViewById(R.id.text)

        init {
            itemView.setOnClickListener {
                val file = files[adapterPosition]
                if (file.isDirectory) {
                    refresh(file.absolutePath)
                }
                if (file.isFile && file.name.endsWith(".md")) {
                    EventBus.getDefault().post(TextUpdateEvent(file.path))
                }
            }
        }

    }

}