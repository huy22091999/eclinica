package im.vector.app.ext.registration


import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.extensions.addFragmentToBackstack
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.GlobitsActivityRegistrationBinding
import im.vector.app.ext.data.model.ItemNotification
import im.vector.app.ext.registration.custom.Notification
import im.vector.app.ext.utils.makeBarsTransparent

import javax.inject.Inject

class RegistrationActivity : VectorBaseActivity<GlobitsActivityRegistrationBinding>(),
    RegistrationViewModel.Factory {

    private val viewModel: RegistrationViewModel by viewModel()
    private lateinit var asyncNotifi: ItemNotification
    var notifi = Notification(this)

    @Inject
    lateinit var registrationViewModelFactory: RegistrationViewModel.Factory

    private val commonOption: (FragmentTransaction) -> Unit = { ft ->
        val enterAnim = R.anim.enter_fade_in
        val exitAnim = R.anim.exit_fade_out

        val popEnterAnim = R.anim.no_anim
        val popExitAnim = R.anim.exit_fade_out

        ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
    }

    override fun getBinding(): GlobitsActivityRegistrationBinding {
        return GlobitsActivityRegistrationBinding.inflate(layoutInflater)
    }

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)
        var body = im.vector.app.ext.data.model.Notification(pageIndex = 0, 10)
        viewModel.handle(RegistrationActions.ShowNotifi(body))


    }



    override fun initUiAndData() {
        super.initUiAndData()

        waitingView = views.waitingView.waitingView

        if (isFirstCreation()) {

            addFragment(R.id.container, RegistrationLandingFragment::class.java)
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

    override fun create(initialState: RegistrationViewState): RegistrationViewModel {
        return registrationViewModelFactory.create(initialState)
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

    ////////////////////////////// PUBLIC //////////////////////////

    fun updateToolbarTitle(title: String): Unit? {
        supportActionBar?.title = ""
        return views.toolbarTitle.setText(title)
    }

    ////////////////////////////// PRIVATE //////////////////////////

    private fun handleRegistrationViewEvents(viewEvent: RegistrationViewEvents) {
        when (viewEvent) {
            is RegistrationViewEvents.LaunchProfileEditFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    RegistrationSection0Fragment::class.java,
                    tag = RegistrationSection0Fragment.TAG,
                    option = commonOption
                )

            is RegistrationViewEvents.LaunchPrepRegistrationFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    RegistrationSection1Fragment::class.java,
                    tag = RegistrationSection1Fragment.TAG,
                    option = commonOption
                )

            is RegistrationViewEvents.LaunchLabTestSelectionFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    RegistrationSection2Fragment::class.java,
                    tag = RegistrationSection2Fragment.TAG,
                    option = commonOption
                )

            is RegistrationViewEvents.LaunchLabTestResultFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    RegistrationSection3Fragment::class.java,
                    tag = RegistrationSection3Fragment.TAG,
                    option = commonOption
                )

            is RegistrationViewEvents.LaunchAppointmentSetupFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    RegistrationSection4Fragment::class.java,
                    tag = RegistrationSection4Fragment.TAG,
                    option = commonOption
                )

            is RegistrationViewEvents.LaunchMedicalExaminationFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    RegistrationSection5Fragment::class.java,
                    tag = RegistrationSection5Fragment.TAG,
                    option = commonOption
                )

            is RegistrationViewEvents.LaunchServiceRatingFragment ->
                addFragmentToBackstack(
                    R.id.container,
                    ServiceRatingFragment::class.java,
                    tag = ServiceRatingFragment.TAG,
                    option = commonOption
                )

            is RegistrationViewEvents.Failure,
            is RegistrationViewEvents.Loading -> Unit

        }
    }

    private fun updateWithState(state: RegistrationViewState) {
        if (state.isLoading()) {
            showWaitingView(getString(R.string.loading))
        } else if(state.notLoading()) {
            hideWaitingView()
        }
        if (state.asyncNotification is Success) {
            if (!this::asyncNotifi.isInitialized) {
                state.asyncNotification.invoke().let {
                    asyncNotifi = it!!
                }
            }
        }

    }


    private fun setupToolbar() {
        views.toolbar.also {
            title = ""
            setSupportActionBar(it)
        }
        views.toolbarTitle.text = getString(R.string.registration)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}
