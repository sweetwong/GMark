package sweet.wong.gmark.ext

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

val ViewPager2.recyclerView: RecyclerView
    get() {
        val clazz = ViewPager2::class.java
        val mRecyclerViewFiled = clazz.getDeclaredField("mRecyclerView")
        mRecyclerViewFiled.isAccessible = true
        return mRecyclerViewFiled.get(this) as RecyclerView
    }