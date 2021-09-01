package sweet.wong.sweetnote.drawer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import sweet.wong.sweetnote.R

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/1
 */
class ProjectAdapter : RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_project, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int = 20

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}