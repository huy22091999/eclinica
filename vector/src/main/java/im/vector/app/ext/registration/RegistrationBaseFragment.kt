package im.vector.app.ext.registration

import android.os.Bundle
import android.view.View
import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment

abstract class RegistrationBaseFragment<VB : ViewBinding> : VectorBaseFragment<VB>(), OnBackPressed {

    protected val viewModel: RegistrationViewModel by activityViewModel()

    open var toolbarTitleRes: Int? = null

    override fun onResume() {
        super.onResume()

        if (toolbarTitleRes != null) {
            (activity as RegistrationActivity).updateToolbarTitle(getString(toolbarTitleRes!!))
        }
    }

    final override fun invalidate() = withState(viewModel) { state ->
        updateWithState(state)
    }

    open fun updateWithState(state: RegistrationViewState) {
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
