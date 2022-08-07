package im.vector.app.ext

import androidx.annotation.CallSuper
import androidx.core.view.isGone
import androidx.core.view.isVisible
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.core.platform.WaitingViewData
import im.vector.app.databinding.GlobitsActivityBinding
import org.matrix.android.sdk.api.session.Session

/**
 * Simple activity with a toolbar, a waiting overlay, and a fragment container and a session.
 */
abstract class BaseActivity : VectorBaseActivity<GlobitsActivityBinding>() {

    final override fun getBinding() = GlobitsActivityBinding.inflate(layoutInflater)

    final override fun getCoordinatorLayout() = views.coordinatorLayout

    lateinit var session: Session

    @CallSuper
    override fun injectWith(injector: ScreenComponent) {
        session = injector.activeSessionHolder().getActiveSession()
    }

    override fun initUiAndData() {
        configureToolbar(views.toolbar)
        waitingView = views.waitingView.waitingView
    }

    /**
     * Displays a progress indicator with a message to the user.
     * Blocks user interactions.
     */
    fun updateWaitingView(data: WaitingViewData?) {
        data?.let {
            views.waitingView.waitingStatusText.text = data.message

            if (data.progress != null && data.progressTotal != null) {
                views.waitingView.waitingHorizontalProgress.isIndeterminate = false
                views.waitingView.waitingHorizontalProgress.progress = data.progress
                views.waitingView.waitingHorizontalProgress.max = data.progressTotal
                views.waitingView.waitingHorizontalProgress.isVisible = true
                views.waitingView.waitingCircularProgress.isVisible = false
            } else if (data.isIndeterminate) {
                views.waitingView.waitingHorizontalProgress.isIndeterminate = true
                views.waitingView.waitingHorizontalProgress.isVisible = true
                views.waitingView.waitingCircularProgress.isVisible = false
            } else {
                views.waitingView.waitingHorizontalProgress.isVisible = false
                views.waitingView.waitingCircularProgress.isVisible = true
            }

            showWaitingView()
        } ?: run {
            hideWaitingView()
        }
    }

    override fun showWaitingView(text: String?) {
        hideKeyboard()
        views.waitingView.waitingStatusText.isGone = views.waitingView.waitingStatusText.text.isNullOrBlank()
        super.showWaitingView(text)
    }

    override fun hideWaitingView() {
        views.waitingView.waitingStatusText.text = null
        views.waitingView.waitingStatusText.isGone = true
        views.waitingView.waitingHorizontalProgress.progress = 0
        views.waitingView.waitingHorizontalProgress.isVisible = false
        super.hideWaitingView()
    }

    override fun onBackPressed() {
        if (waitingView!!.isVisible) {
            // ignore
            return
        }
        super.onBackPressed()
    }
}
