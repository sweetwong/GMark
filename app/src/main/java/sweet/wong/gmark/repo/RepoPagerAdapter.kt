package sweet.wong.gmark.repo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class RepoPagerAdapter(
    fa: FragmentActivity,
    private val fragments: List<RepoFragment>,
) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]

}