package im.vector.app.ext.profile

import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.airbnb.mvrx.viewModel
import com.google.android.material.tabs.TabLayoutMediator
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.addFragment
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.GlobitsActivityProfileBinding
import im.vector.app.ext.registration.*
import im.vector.app.ext.utils.adjustToolbarPadding
import im.vector.app.ext.utils.makeBarsTransparent
import javax.inject.Inject

class ProfileActivity : VectorBaseActivity<GlobitsActivityProfileBinding>(), ProfileViewModel.Factory {

    private val viewModel: ProfileViewModel by viewModel()

    private lateinit var editFormAdapter: EditScreenViewPagerAdapter
    private lateinit var viewPager: ViewPager2

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModel.Factory

    @Inject
    lateinit var registrationViewModelFactory: RegistrationViewModel.Factory
//    override fun getMenuRes() = R.menu._globits_menu_save

    override fun getBinding(): GlobitsActivityProfileBinding {
        return GlobitsActivityProfileBinding.inflate(layoutInflater)
    }

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)

    }

    override fun initUiAndData() {
        super.initUiAndData()

        waitingView = views.waitingView.waitingView

        setupToolbar()
        setupViewPager()
//        setupPageChangedListener()

        viewModel.subscribe(this) {
            updateWithState(it)
        }

    }

    ////////////////////////////// PRIVATE //////////////////////////

    private fun setupToolbar() {
        views.toolbar.also {
            // adjust the drawer padding
//            it.adjustToolbarPadding(this)
            title = getString(R.string.personal_profile)
            setSupportActionBar(it)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    // set up the view pager
    private fun setupViewPager() {
        editFormAdapter = EditScreenViewPagerAdapter(this)

        viewPager = views.pager
        viewPager.adapter = editFormAdapter
        viewPager.offscreenPageLimit = 3

        // tabs
        val tabNames = listOf<String>("Thông tin cá nhân", "Thông tin sàng lọc", "Phiếu đồng thuận")
        val tabLayout = views.tabLayout
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabNames.get(position)
        }.attach()
    }

    // on view pager page changed
//    private fun setupPageChangedListener() {
//        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//            }
//        })
//    }

    override fun create(initialState: ProfileViewState): ProfileViewModel {
        return profileViewModelFactory.create(initialState)
    }

    private fun updateWithState(state: ProfileViewState) {
        if (state.isLoading()) {
            showWaitingView(getString(R.string.loading))
        } else {
            hideWaitingView()
        }
    }

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

}

class EditScreenViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    companion object {
        const val TAB_COUNT = 3
    }

    private val fragments = mutableListOf<Fragment>()

    override fun getItemCount(): Int {
        return TAB_COUNT
    }

    fun getFragment(position: Int): Fragment {
        return fragments.get(position)
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> EditProfileSection0Fragment()
            1 -> EditProfileSection1Fragment()
            else -> EditProfileSection2Fragment()
        }

        fragments.add(position, fragment)
        return fragment
    }
}
