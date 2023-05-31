package  com.photoshooto.base

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    var rootView: View? = null

    protected fun addFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        fragmentTag: String
    ) {
        childFragmentManager
            .beginTransaction()
            .add(containerViewId, fragment, fragmentTag)
            .disallowAddToBackStack()
            .commit()
    }

    protected fun replaceFragment(
        @IdRes containerViewId: Int,
        fragment: Fragment,
        fragmentTag: String,
        addToBackStack: Boolean? = false
    ) {
        childFragmentManager
            .beginTransaction()
            .replace(containerViewId, fragment, fragmentTag)
            .addToBackStack(if (addToBackStack!!) fragment::class.java.simpleName else null)
            .commit()
    }

    fun showProgress() {
        (activity as? BaseActivity)?.showProgress()
    }

    fun hideProgress() {
        (activity as? BaseActivity)?.hideProgress()
    }
}