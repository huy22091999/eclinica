package im.vector.app.ext.laboratory

import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.viewModel
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.extensions.addFragmentToBackstack
import im.vector.app.core.extensions.commitTransaction
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.GlobitsActivityBinding
import im.vector.app.ext.home.GlobitsHomeViewModel
import im.vector.app.ext.registration.RegistrationLandingFragment
import im.vector.app.ext.registration.RegistrationSection0Fragment
import im.vector.app.ext.registration.RegistrationViewEvents
import im.vector.app.ext.registration.RegistrationViewState
import im.vector.app.ext.utils.makeBarsTransparent
import javax.inject.Inject
import kotlin.reflect.KClass

class LaboratoryActivity :
    VectorBaseActivity<GlobitsActivityBinding>(),
    LaboratoryViewModel.Factory
{

    private val viewModel: LaboratoryViewModel by viewModel()

    @Inject
    lateinit var laboratoryViewModelFactory: LaboratoryViewModel.Factory

    @CallSuper
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

    override fun getBinding() : GlobitsActivityBinding {
        return GlobitsActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)
    }

    override fun initUiAndData() {
        super.initUiAndData()

        waitingView = views.waitingView.waitingView

        if (isFirstCreation()) {
            addFragment(R.id.container, LaboratoryFragment::class.java)
//            showFragment(LaboratoryFragment::class, Bundle.EMPTY)
        }

        setupToolbar()

        viewModel.subscribe(this) {
            updateWithState(it)
        }

        viewModel.observeViewEvents {
            handleRegistrationViewEvents(it)
        }
    }

    override fun onBackPressed() {
        if (waitingView!!.isVisible) {
            // ignore
            return
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    private fun updateWithState(state: LaboratoryViewState) {
        if (state.isLoading()) {
            showWaitingView(getString(R.string.loading))
        } else {
            hideWaitingView()
        }
    }

    private fun handleRegistrationViewEvents(viewEvents: LaboratoryViewEvents) {
        when (viewEvents) {
            is LaboratoryViewEvents.LaunchLabTestTemplateDetailFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    LabTestTemplateDetailFragment::class.java,
                    tag = LabTestTemplateDetailFragment.TAG,
                    option = commonOption
                )

            is LaboratoryViewEvents.Failure,
            is LaboratoryViewEvents.Loading -> Unit
        }
    }

    ////////////////////////////// PRIVATE //////////////////////////

    private fun setupToolbar() {
        views.toolbar.also {
            title = getString(R.string.lab_tests)
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
    override fun showWaitingView(text: String?) {
        hideKeyboard()
        super.showWaitingView(text)

        views.waitingView.waitingHorizontalProgress.isIndeterminate = true
        views.waitingView.waitingHorizontalProgress.isVisible = true
        views.waitingView.waitingStatusText.isGone = views.waitingView.waitingStatusText.text.isNullOrBlank()
    }

    override fun hideWaitingView() {
        views.waitingView.waitingStatusText.text = null
        views.waitingView.waitingStatusText.isGone = true
        views.waitingView.waitingHorizontalProgress.progress = 0
        views.waitingView.waitingHorizontalProgress.isVisible = false
        super.hideWaitingView()
    }

    fun updateToolbarTitle(title: String): Unit? {
        return supportActionBar?.setTitle(title)
    }

    override fun create(initialState: LaboratoryViewState): LaboratoryViewModel {
        return laboratoryViewModelFactory.create(initialState)
    }
}
