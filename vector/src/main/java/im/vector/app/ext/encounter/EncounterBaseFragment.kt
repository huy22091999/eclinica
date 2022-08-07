package im.vector.app.ext.encounter

import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment

abstract class EncounterBaseFragment<VB : ViewBinding> : VectorBaseFragment<VB>(), OnBackPressed {

    protected val viewModel: EncounterViewModel by activityViewModel()

    open var toolbarTitleRes: Int? = null

    override fun onResume() {
        super.onResume()

//        if (toolbarTitleRes != null) {
//            (activity as EncounterActivity).updateToolbarTitle(getString(toolbarTitleRes!!))
//        }
    }

    final override fun invalidate() = withState(viewModel) { state ->
        updateWithState(state)
    }

    open fun updateWithState(state: EncounterViewState) {
        // No op by default
    }

    override fun onBackPressed(toolbarButton: Boolean): Boolean {
        resetViewModel()
        return false
    }

    // Reset any modification on the loginViewModel by the current fragment
    abstract fun resetViewModel()

    // Update edited data
    abstract fun updateData()
}
