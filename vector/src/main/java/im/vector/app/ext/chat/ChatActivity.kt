package im.vector.app.ext.chat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.airbnb.mvrx.viewModel
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.replaceFragment
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.GlobitsActivityChatBinding
import im.vector.app.ext.utils.adjustToolbarPadding
import im.vector.app.ext.utils.makeBarsTransparent
import im.vector.app.features.home.*
import im.vector.app.features.workers.signout.ServerBackupStatusViewModel
import im.vector.app.features.workers.signout.ServerBackupStatusViewState
import javax.inject.Inject

class ChatActivity :
    VectorBaseActivity<GlobitsActivityChatBinding>(),
    UnknownDeviceDetectorSharedViewModel.Factory,
    ServerBackupStatusViewModel.Factory,
    UnreadMessagesSharedViewModel.Factory {

    private lateinit var sharedActionViewModel: HomeSharedActionViewModel

    private val homeActivityViewModel: HomeActivityViewModel by viewModel()
    private val serverBackupStatusViewModel: ServerBackupStatusViewModel by viewModel()

    @Inject
    lateinit var viewModelFactory: HomeActivityViewModel.Factory

    @Inject
    lateinit var serverBackupviewModelFactory: ServerBackupStatusViewModel.Factory

    @Inject
    lateinit var unknownDeviceViewModelFactory: UnknownDeviceDetectorSharedViewModel.Factory

    @Inject
    lateinit var unreadMessagesSharedViewModelFactory: UnreadMessagesSharedViewModel.Factory

    override fun getBinding() = GlobitsActivityChatBinding.inflate(layoutInflater)

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        makeBarsTransparent()
        super.onCreate(savedInstanceState)

        sharedActionViewModel = viewModelProvider.get(HomeSharedActionViewModel::class.java)
        if (isFirstCreation()) {
            replaceFragment(R.id.homeDetailFragmentContainer, HomeDetailFragment::class.java)
        }

        setupToolbar()
    }

    // --------------------------------------------
    // Public
    // --------------------------------------------
    fun updateToolbarTitle(title: String): Unit? {
        return supportActionBar?.setTitle(title)
    }

    // --------------------------------------------
    // Private
    // --------------------------------------------

    private fun setupToolbar() {
        views.toolbar.also {
            // adjust the drawer padding
//            it.adjustToolbarPadding(this)
            title = getString(R.string.title_activity_home)
            setSupportActionBar(it)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ChatActivity::class.java)
        }
    }

    // --------------------------------------------
    // override
    // --------------------------------------------
    override fun create(initialState: UnknownDevicesState): UnknownDeviceDetectorSharedViewModel {
        return unknownDeviceViewModelFactory.create(initialState)
    }

    override fun create(initialState: UnreadMessagesState): UnreadMessagesSharedViewModel {
        return unreadMessagesSharedViewModelFactory.create(initialState)
    }

    override fun create(initialState: ServerBackupStatusViewState): ServerBackupStatusViewModel {
        return serverBackupviewModelFactory.create(initialState)
    }
}
