package sweet.wong.sweetnote.repodetail.drawer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.sweetnote.repodetail.RepoDetailViewModel
import sweet.wong.sweetnote.R
import java.io.File

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/1
 */
class DrawerProjectAdapter(context: Context) :
    RecyclerView.Adapter<DrawerProjectAdapter.ViewHolder>() {

    private val activity = context as AppCompatActivity

    private val viewModel: RepoDetailViewModel by activity.viewModels()

    private val files = mutableListOf<File>()

    init {
        viewModel.drawerFold.observe(activity) {
            files.clear()
            File(it).listFiles()?.toList()?.let { list ->
                files.addAll(list)
            }
            notifyDataSetChanged()
        }
    }

    fun onBackPressed() {
        viewModel.drawerFold.value = File(viewModel.drawerFold.value ?: return).parent ?: return
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
                    viewModel.drawerFold.value = file.absolutePath
                }
                if (file.isFile && file.name.endsWith(".md")) {
                    viewModel.path.value = file.absolutePath
                }
            }
        }

    }

}