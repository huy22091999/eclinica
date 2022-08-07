package im.vector.app.ext.examination

import androidx.viewbinding.ViewBinding
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.core.platform.VectorBaseFragment

abstract class ExaminationBaseFragment<VB : ViewBinding> : VectorBaseFragment<VB>() {

    protected val viewModel: ExaminationViewModel by activityViewModel()

    open var toolbarTitleRes: Int? = null

    override fun onResume() {
        super.onResume()

        if (toolbarTitleRes != null) {
            (activity as ExaminationActivity).updateToolbarTitle(getString(toolbarTitleRes!!))
        }
    }

    final override fun invalidate() = withState(viewModel) { state ->
        updateWithState(state)
    }

    open fun updateWithState(state: ExaminationViewState) {
        // No op by default
    }
}
