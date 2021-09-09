package sweet.wong.gmark.utils

import androidx.databinding.ObservableList

/**
 * TODO: Add Description
 *
 * @author sweetwang 2021/9/9
 */
abstract class OnListChangedCallback<T : ObservableList<*>> :
    ObservableList.OnListChangedCallback<T>() {

    override fun onChanged(sender: T) {
    }

    override fun onItemRangeChanged(
        sender: T,
        positionStart: Int,
        itemCount: Int
    ) {
    }

    override fun onItemRangeInserted(
        sender: T,
        positionStart: Int,
        itemCount: Int
    ) {
    }

    override fun onItemRangeMoved(
        sender: T,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
    }

    override fun onItemRangeRemoved(
        sender: T,
        positionStart: Int,
        itemCount: Int
    ) {
    }
}