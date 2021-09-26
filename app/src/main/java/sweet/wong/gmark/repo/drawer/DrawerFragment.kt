package sweet.wong.gmark.repo.drawer

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import sweet.wong.gmark.R
import sweet.wong.gmark.base.BaseFragment
import sweet.wong.gmark.repo.RepoViewModel

abstract class DrawerFragment<T : ViewBinding> : BaseFragment<T>() {

    protected lateinit var toolbar: Toolbar
    protected lateinit var repoViewModel: RepoViewModel

    protected open val menuRes: Int
        @MenuRes get() = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repoViewModel = ViewModelProvider(requireActivity())[RepoViewModel::class.java]
        toolbar = requireActivity().findViewById(R.id.drawer_toolbar)
        toolbar.menu.clear()

        if (menuRes != 0) {
            toolbar.inflateMenu(menuRes)
            toolbar.setOnMenuItemClickListener {
                onMenuItemSelected(it)
                true
            }
        }
    }

    protected open fun onMenuItemSelected(menuItem: MenuItem) {}

}