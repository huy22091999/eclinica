package im.vector.app.ext

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.annotation.CallSuper
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import com.airbnb.mvrx.*
import com.google.android.material.navigation.NavigationView
import im.vector.app.AppStateHandler
import im.vector.app.R
import im.vector.app.core.di.ActiveSessionHolder
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.hideKeyboard
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.core.pushers.PushersManager
import im.vector.app.databinding.GlobitsActivityHomeBinding
import im.vector.app.ext.about.AboutActivity
import im.vector.app.ext.chat.ChatActivity
import im.vector.app.ext.data.model.PatientInfo
import im.vector.app.ext.data.network.SessionManager
import im.vector.app.ext.encounter.EncounterActivity
import im.vector.app.ext.home.GlobitsHomeActions
import im.vector.app.ext.home.GlobitsHomeViewModel
import im.vector.app.ext.home.GlobitsHomeViewState
import im.vector.app.ext.laboratory.LaboratoryActivity
import im.vector.app.ext.examination.ExaminationActivity
import im.vector.app.ext.profile.ProfileActivity
import im.vector.app.ext.registration.service.MyService
import im.vector.app.ext.utils.*
import im.vector.app.features.home.*
import im.vector.app.features.notifications.NotificationDrawerManager
import im.vector.app.features.permalink.PermalinkHandler
import im.vector.app.features.popup.PopupAlertManager
import im.vector.app.features.rageshake.VectorUncaughtExceptionHandler
import im.vector.app.features.settings.VectorPreferences
import im.vector.app.features.workers.signout.ServerBackupStatusViewModel
import im.vector.app.features.workers.signout.ServerBackupStatusViewState
import im.vector.app.features.workers.signout.SignOutUiWorker
import org.matrix.android.sdk.api.session.Session
import javax.inject.Inject


class GlobitsHomeActivity :
    VectorBaseActivity<GlobitsActivityHomeBinding>(),
    UnknownDeviceDetectorSharedViewModel.Factory,
    GlobitsHomeViewModel.Factory,
    HomeDetailViewModel.Factory,
    ServerBackupStatusViewModel.Factory,
    UnreadMessagesSharedViewModel.Factory,
    NavigationView.OnNavigationItemSelectedListener {

    private lateinit var sharedActionViewModel: HomeSharedActionViewModel

    private val homeDetailViewModel: HomeDetailViewModel by viewModel()

    private val viewModel: GlobitsHomeViewModel by viewModel()


    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    @Inject
    lateinit var viewModelFactory: HomeActivityViewModel.Factory

    @Inject
    lateinit var serverBackupviewModelFactory: ServerBackupStatusViewModel.Factory

    @Inject
    lateinit var activeSessionHolder: ActiveSessionHolder

    @Inject
    lateinit var vectorUncaughtExceptionHandler: VectorUncaughtExceptionHandler

    @Inject
    lateinit var pushManager: PushersManager

    @Inject
    lateinit var notificationDrawerManager: NotificationDrawerManager

    @Inject
    lateinit var vectorPreferences: VectorPreferences

    @Inject
    lateinit var popupAlertManager: PopupAlertManager

    @Inject
    lateinit var shortcutsHandler: ShortcutsHandler

    @Inject
    lateinit var unknownDeviceViewModelFactory: UnknownDeviceDetectorSharedViewModel.Factory

    @Inject
    lateinit var unreadMessagesSharedViewModelFactory: UnreadMessagesSharedViewModel.Factory

    @Inject
    lateinit var globitsHomeViewModelFactory: GlobitsHomeViewModel.Factory

    @Inject
    lateinit var homeDetailViewModelFactory: HomeDetailViewModel.Factory

    @Inject
    lateinit var permalinkHandler: PermalinkHandler

    @Inject
    lateinit var avatarRenderer: AvatarRenderer

    @Inject
    lateinit var appStateHandler: AppStateHandler

    @Inject
    lateinit var session: Session

    @CallSuper
    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)

        sharedActionViewModel = viewModelProvider.get(HomeSharedActionViewModel::class.java)

        SessionManager(applicationContext).saveAuthToken(session.sessionParams.credentials.accessToken)

        viewModel.subscribe(this) {
            updateWithState(it)
        }

//        homeDetailViewModel.subscribe(this) {
//            views.appBarMain.homeFab.count = it.notificationCountPeople + it.notificationCountRooms
//        }

        setupToolbar()
//        setupDrawer()
//        setupBottomNav()
        //setupChatFab()
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

    override fun onResume() {
        super.onResume()
        viewModel.handle(GlobitsHomeActions.InitAction)

        val serviceIntent = Intent(this, MyService::class.java)
        startService(serviceIntent)

//        navController.navigate(R.id.nav_home)
    }

    ////////////////////////// PRIVATE /////////////////////////////////

    private fun updateWithState(state: GlobitsHomeViewState) {
        when (state.loginPatient) {
            is Loading -> showWaitingView(getString(R.string.loading))

            is Success -> {
                val patient: PatientInfo = state.loginPatient.invoke()
                setupUserInfo(patient)

                hideWaitingView()
            }
            else -> Unit
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun setupToolbar() {
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor= R.color.eclinica_background_color

        // adjust the drawer padding
//        views.appBarMain.toolbar.also {
//            setSupportActionBar(it)
//
//            // Update overflow icon color
//            val ovfIcon = it.overflowIcon.apply {
//                val color = resources.getColor(R.color.alpha_white_50, theme)
//                this?.setTint(color)
//            }
//
//            it.overflowIcon = ovfIcon
//        }
//
//        supportActionBar?.also {
//            it.title = ""
//        }
//
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    private fun setupDrawer() {
        navView = views.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        drawerLayout = views.drawerLayout

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        // hamburger button
        drawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.open_settings, R.string.action_close)
    }

    private fun setupBottomNav() {
//        views.appBarMain.bottomAppBar.adjustBottomNavPadding(this)
    }

    //    private fun setupChatFab() {
//        val fab = views.appBarMain.homeFab
//
//        // adjust position
//        val statusHeight = getStatusBarHeight()
//        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            fab.setMargins(fab.marginLeft, fab.marginTop, fab.marginRight, fab.marginBottom + statusHeight)
//        } else {
//            fab.setMargins(fab.marginLeft, fab.marginTop, fab.marginRight + statusHeight, fab.marginBottom)
//        }
//
//        // listener
//        fab.clickWithThrottle {
//
//        }
//    }
    fun openChat() {
        startActivity(ChatActivity.newIntent(applicationContext))
    }

    ////////////////////////// PUBLIC ////////////////////////////////

    fun navigateTo(fragmentId: Int) {
        navController.navigate(fragmentId)
    }

    fun openSettings() {
        sharedActionViewModel.post(HomeActivitySharedAction.CloseDrawer)
        navigator.openSettings(this)
    }

    fun openAbout() {
        val intent = Intent(applicationContext, AboutActivity::class.java)
        startActivity(intent)
    }

    fun setupUserInfo(patient: PatientInfo) {
//        val navHeader = views.navView.getHeaderView(0)
//        val avatar = navHeader.findViewById<ImageView>(R.id.profile_image)
//        val accountName = navHeader.findViewById<TextView>(R.id.account_name)
//        val accountId = navHeader.findViewById<TextView>(R.id.account_id)

        val accountName = views.appBarMain.accountName
        val collapseToolbar = views.appBarMain.collapsingToolbarLayout

        patient.let {
            accountName.text = it.fullName

            collapseToolbar.apply {
                this.title = it.fullName

                setCollapsedTitleTypeface(
                    ResourcesCompat.getFont(
                        context,
                        R.font.quicksand_semibold
                    )
                )
                setExpandedTitleTypeface(
                    ResourcesCompat.getFont(
                        context,
                        R.font.quicksand_semibold
                    )
                )

                this.setExpandedTitleColor(
                    ContextCompat.getColor(
                        context,
                        android.R.color.transparent
                    )
                )
                this.setCollapsedTitleTextColor(Color.rgb(255, 255, 255))

            }
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var handled = NavigationUI.onNavDestinationSelected(item, navController)
        if (!handled) {
            when (item.itemId) {
                R.id.nav_appointments -> {
                    val intent = Intent(applicationContext, EncounterActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_lab_tests -> {
                    val intent = Intent(applicationContext, LaboratoryActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_drug_prescriptions -> {
                    val intent = Intent(applicationContext, ExaminationActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_personal_profile -> {
                    val intent = Intent(applicationContext, ProfileActivity::class.java)
                    startActivity(intent)
                }

                R.id.nav_settings -> {
                    sharedActionViewModel.post(HomeActivitySharedAction.CloseDrawer)
                    navigator.openSettings(this)
                }

                R.id.nav_logout -> {
                    SignOutUiWorker(this).perform()
                }
            }
            handled = true
        }

//        drawerLayout.closeDrawer(GravityCompat.START)

        return handled
    }

//    override fun onPostCreate(savedInstanceState: Bundle?) {
//        super.onPostCreate(savedInstanceState)
//        drawerToggle.syncState()
//    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
//    }

//    override fun onBackPressed() {
//        if (views.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            views.drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }

    override fun getBinding() = GlobitsActivityHomeBinding.inflate(layoutInflater)

    override fun create(initialState: UnknownDevicesState): UnknownDeviceDetectorSharedViewModel {
        return unknownDeviceViewModelFactory.create(initialState)
    }

    override fun create(initialState: ServerBackupStatusViewState): ServerBackupStatusViewModel {
        return serverBackupviewModelFactory.create(initialState)
    }

    override fun create(initialState: UnreadMessagesState): UnreadMessagesSharedViewModel {
        return unreadMessagesSharedViewModelFactory.create(initialState)
    }

    override fun create(initialState: GlobitsHomeViewState): GlobitsHomeViewModel {
        return globitsHomeViewModelFactory.create(initialState)
    }

    override fun create(initialState: HomeDetailViewState): HomeDetailViewModel {
        return homeDetailViewModelFactory.create(initialState)
    }

    companion object {
        fun newIntent(
            context: Context,
            clearNotification: Boolean = false,
            accountCreation: Boolean = false
        ): Intent {
            val args = HomeActivityArgs(
                clearNotification = clearNotification,
                accountCreation = accountCreation
            )

            return Intent(context, GlobitsHomeActivity::class.java)
                .apply {
                    putExtra(MvRx.KEY_ARG, args)
                }
        }

    }

}
