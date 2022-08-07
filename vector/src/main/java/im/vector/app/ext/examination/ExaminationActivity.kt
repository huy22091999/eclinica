package im.vector.app.ext.examination

import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.viewModel
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.extensions.addFragmentToBackstack
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.GlobitsActivityExaminationBinding
import im.vector.app.ext.utils.makeBarsTransparent
import javax.inject.Inject

class ExaminationActivity : VectorBaseActivity<GlobitsActivityExaminationBinding>(),
    ExaminationViewModel.Factory {

    private val viewModel: ExaminationViewModel by viewModel()

    @Inject
    lateinit var examinationViewModelFactory: ExaminationViewModel.Factory

    override fun getBinding(): GlobitsActivityExaminationBinding {
        return GlobitsActivityExaminationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)

//        if (isFirstCreation()) {
//            showFragment(PrescriptionFragment::class, Bundle.EMPTY)
//        }
//
//        setupToolbar()
    }

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    private val commonOption: (FragmentTransaction) -> Unit = { ft ->
        val enterAnim = R.anim.enter_fade_in
        val exitAnim = R.anim.exit_fade_out

        val popEnterAnim = R.anim.no_anim
        val popExitAnim = R.anim.exit_fade_out

        ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
    }

    override fun initUiAndData() {
        super.initUiAndData()

        waitingView = views.waitingView.waitingView

        if (isFirstCreation()) {
            addFragment(R.id.container, ExaminationFragment::class.java)
        }

        setupToolbar()

        viewModel.subscribe(this) {
            updateWithState(it)
        }

        viewModel.observeViewEvents {
            handleRegistrationViewEvents(it)
        }

    }

    private fun updateWithState(state: ExaminationViewState) {
        if (state.isLoading()) {
            showWaitingView(getString(R.string.loading))
        } else {
            hideWaitingView()
        }
    }

    private fun handleRegistrationViewEvents(viewEvent: ExaminationViewEvents) {
        when (viewEvent) {
            is ExaminationViewEvents.LaunchDetailFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    ExaminationListFragment::class.java,
                    tag = ExaminationListFragment.TAG,
                    option = commonOption
                )
            is ExaminationViewEvents.LaunchUpdateMethodReceive ->
                addFragmentToBackstack(
                    R.id.container,
                    ExaminationUpdateMethodFragment::class.java,
                    tag = ExaminationUpdateMethodFragment.TAG,
                    option = commonOption
                )
        }
    }

    ////////////////////////////// PRIVATE //////////////////////////

    private fun setupToolbar() {
        views.toolbar.also {
            // adjust the drawer padding
//            it.adjustToolbarPadding(this)
            title = getString(R.string.drug_prescriptions)
            setSupportActionBar(it)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

//    private fun showFragment(fragmentClass: KClass<out Fragment>, bundle: Bundle) {
//        if (supportFragmentManager.findFragmentByTag(fragmentClass.simpleName) == null) {
//            supportFragmentManager.commitTransaction {
//                setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
//                replace(
//                    R.id.container,
//                    fragmentClass.java,
//                    bundle,
//                    fragmentClass.simpleName
//                )
//            }
//        }
//    }

    fun updateToolbarTitle(title: String): Unit? {
        return supportActionBar?.setTitle(title)
    }

    override fun showWaitingView(text: String?) {
        hideKeyboard()
        super.showWaitingView(text)

        views.waitingView.waitingHorizontalProgress.isIndeterminate = true
        views.waitingView.waitingHorizontalProgress.isVisible = true
        views.waitingView.waitingStatusText.isGone =
            views.waitingView.waitingStatusText.text.isNullOrBlank()
    }

    override fun hideWaitingView() {
        views.waitingView.waitingStatusText.text = null
        views.waitingView.waitingStatusText.isGone = true
        views.waitingView.waitingHorizontalProgress.progress = 0
        views.waitingView.waitingHorizontalProgress.isVisible = false
        super.hideWaitingView()
    }

    override fun create(initialState: ExaminationViewState): ExaminationViewModel {
        return examinationViewModelFactory.create(initialState)
    }
}
