package sweet.wong.gmark.search

import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import sweet.wong.gmark.R
import sweet.wong.gmark.core.App
import sweet.wong.gmark.core.toast
import sweet.wong.gmark.databinding.RecycleItemFileSearchBinding
import sweet.wong.gmark.ext.getColorFromAttr
import sweet.wong.gmark.ext.inflater
import sweet.wong.gmark.utils.DefaultDiffUtilCallback

class FileSearchAdapter(
    private val activity: AppCompatActivity,
    private val viewModel: SearchViewModel
) :
    ListAdapter<FileSearchResult, FileSearchAdapter.VH>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        RecycleItemFileSearchBinding.inflate(parent.inflater, parent, false)
    )

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    inner class VH(private val binding: RecycleItemFileSearchBinding) : ViewHolder(binding.root) {

        private val color = App.app.getColorFromAttr(R.attr.colorPrimary)

        private var keywordObserver: Observer<String>? = null

        init {
            binding.textView.setOnClickListener {
                toast("不错")
            }
        }

        fun bind(data: FileSearchResult) {
            keywordObserver?.let {
                viewModel.keyword.removeObserver(it)
            }

            val observer = Observer<String> { keyword ->
                val spannable = SpannableString(data.relativePath)
                val span = ForegroundColorSpan(color)
                val start = data.relativePath.lowercase().indexOf(keyword.lowercase())
                if (start != -1) {
                    spannable.setSpan(
                        span,
                        start,
                        start + keyword.length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                    )
                }
                binding.textView.text = spannable
            }
            viewModel.keyword.observe(activity, observer)
            keywordObserver = observer
        }

    }
}