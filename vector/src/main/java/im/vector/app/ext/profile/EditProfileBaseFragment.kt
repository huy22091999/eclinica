package im.vector.app.ext.profile

import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.core.platform.OnBackPressed
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.ext.registration.RegistrationViewModel
import im.vector.app.ext.registration.RegistrationViewState

abstract class EditProfileBaseFragment<VB : ViewBinding> : VectorBaseFragment<VB>() {

    protected val viewModel: ProfileViewModel by activityViewModel()

    final override fun invalidate() = withState(viewModel) { state ->
        updateWithState(state)
    }

    open fun updateWithState(state: ProfileViewState) {
        // No op by default
    }

    // Update edited data
    abstract fun updateData()
}
