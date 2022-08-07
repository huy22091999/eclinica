package im.vector.app.ext.encounter

import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.viewModel
import com.google.android.material.tabs.TabLayoutMediator
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.GlobitsActivityBinding
import im.vector.app.ext.laboratory.LaboratoryViewModel
import im.vector.app.ext.laboratory.LaboratoryViewState
import im.vector.app.ext.utils.makeBarsTransparent
import javax.inject.Inject

class EncounterActivity : VectorBaseActivity<GlobitsActivityBinding>(), EncounterViewModel.Factory,
    LaboratoryViewModel.Factory {

    private val viewModelLab: LaboratoryViewModel by viewModel()

    @Inject
    lateinit var laboratoryViewModelFactory: LaboratoryViewModel.Factory

    private val viewModel: EncounterViewModel by viewModel()

    @Inject
    lateinit var encounterViewModelFactory: EncounterViewModel.Factory
    private lateinit var adapterFragment: EncounterAndLaboratoryPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)
    }

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    override fun initUiAndData() {
        super.initUiAndData()

        waitingView = views.waitingView.waitingView

        setupViewPager()

        setupToolbar()

        viewModel.subscribe(this) {
            updateWithState(it)
        }
    }

    private fun updateWithState(state: EncounterViewState) {
        if (state.isLoading()) {
            showWaitingView(getString(R.string.loading))
        } else {
            hideWaitingView()
        }
    }


    override fun create(initialState: EncounterViewState): EncounterViewModel {
        return encounterViewModelFactory.create(initialState)
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

    ////////////////////////////// PRIVATE //////////////////////////

    private fun setupToolbar() {
        views.toolbar.also {
            title=getString(R.string.result_examination_labtest)
            setSupportActionBar(it)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
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

    ////////////// PUBLIC ///////////////////
    fun updateToolbarTitle(title: String): Unit? {
        return supportActionBar?.setTitle(title)
    }

    private fun setupViewPager() {
        adapterFragment = EncounterAndLaboratoryPagerAdapter(this)
        viewPager = views.container
        viewPager.adapter = adapterFragment
        viewPager.offscreenPageLimit = 2

        // tabs
        val tabNames = listOf(
            getString(R.string.encouter),
            getString(R.string.laboratory),
        )

        val tabLayout = views.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames[position]
        }.attach()
    }

    override fun getBinding(): GlobitsActivityBinding {
        return GlobitsActivityBinding.inflate(layoutInflater)
    }

    companion object {
        lateinit var viewPager: ViewPager2
    }

    override fun create(initialState: LaboratoryViewState): LaboratoryViewModel {
        return laboratoryViewModelFactory.create(initialState)
    }

}
