package im.vector.app.ext.conference

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.fragment.app.FragmentTransaction
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.viewModel
import com.airbnb.mvrx.withState
import com.facebook.react.modules.core.PermissionListener
import im.vector.app.R
import im.vector.app.core.di.ScreenComponent
import im.vector.app.core.extensions.addFragmentToBackstack
import im.vector.app.core.extensions.exhaustive
import im.vector.app.core.platform.VectorBaseActivity
import im.vector.app.databinding.ActivityJitsiBinding
import im.vector.app.ext.utils.snackbar
import org.jitsi.meet.sdk.*
import org.matrix.android.sdk.api.extensions.tryOrNull
import timber.log.Timber
import javax.inject.Inject


class ConferenceActivity : VectorBaseActivity<ActivityJitsiBinding>(), JitsiMeetActivityInterface
    , ConferenceViewModel.Factory {
//    @Parcelize
//    data class Args(
//        val roomId: String,
//        val enableVideo: Boolean
//    ) : Parcelable

    @Inject
    lateinit var conferenceViewModelFactory: ConferenceViewModel.Factory

    private val commonOption: (FragmentTransaction) -> Unit = { ft ->
        val enterAnim = R.anim.enter_fade_in
        val exitAnim = R.anim.exit_fade_out

        val popEnterAnim = R.anim.no_anim
        val popExitAnim = R.anim.exit_fade_out

        ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
    }

    override fun injectWith(injector: ScreenComponent) {
        injector.inject(this)
    }

    private var view: JitsiMeetView? = null
    private val conferenceViewModel: ConferenceViewModel by viewModel()
    override fun getBinding() = ActivityJitsiBinding.inflate(layoutInflater)
    override fun requestPermissions(permissions: Array<out String>?, requestCode: Int, listener: PermissionListener?) {
        JitsiMeetActivityDelegate.requestPermissions(this, permissions, requestCode, listener)
    }
    private fun registerForBroadcastMessages() {
        val intentFilter = IntentFilter()
        for (type in BroadcastEvent.Type.values()) {
            intentFilter.addAction(type.action)
        }
        tryOrNull("Unable to register receiver") {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val conferenceId = intent.getStringExtra("conferenceId")
        view = JitsiMeetView(this)
        val options = JitsiMeetConferenceOptions.Builder()
//            .setRoom("https://jitsi.riot.im/$conferenceId")
            .setRoom("https://meet.oceantech.com.vn:9143/$conferenceId")
            .setFeatureFlag("chat.enabled",false)
            .build()

        view?.join(options)
//        setContentView(view)
        views.jitsiLayout.removeAllViews()
        views.jitsiLayout.addView(view)

        setupToolbar()



        addFragmentToBackstack(
            R.id.fragment_content,
            MedicalExaminationFragment::class.java,
            tag = MedicalExaminationFragment.TAG,
            option = commonOption
        )
        var isStarRate=false
        conferenceViewModel.observeViewEvents {
            when (it) {
                    ConferenceViewEvents.Finish -> finish()
                    ConferenceViewEvents.LeaveConference -> handleLeaveConference()
                    //TODO cần viết 1 fragment khác để đánh giá
                    is ConferenceViewEvents.LaunchServiceRatingFragment ->
                    {
                        if(!isStarRate) {
                            isStarRate=true
                            addFragmentToBackstack(
                                R.id.fragment_content,
                                ServiceRatingsFragment
                                ::class.java,
                                tag = ServiceRatingsFragment.TAG,
                                option = commonOption
                            )
                        }else{
                        }
                    }

                    is ConferenceViewEvents.EligibleCustomerComplete -> {
                        withState(conferenceViewModel) {
                            when (it.asyncMedicalExamination) {
                                is Success -> this.snackbar(getString(R.string.eligible_customer_successfully_saved))
                                is Fail -> this.snackbar(getString(R.string.data_save_failure))
                                else -> Unit
                            }
                        }
                    }

                    is ConferenceViewEvents.Failure,
                    is ConferenceViewEvents.Loading -> Unit
                    else -> Unit


            }.exhaustive
        }
        registerForBroadcastMessages()
    }
    private fun unregisterForBroadcastMessages() {
        tryOrNull("Unable to unregister receiver") {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        }
    }
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { onBroadcastReceived(it) }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    private fun onBroadcastReceived(intent: Intent) {
        val event = BroadcastEvent(intent)
        Timber.v("Broadcast received: ${event.type}")
        when (event.type) {
            BroadcastEvent.Type.CONFERENCE_TERMINATED -> onConferenceTerminated(event.data)
            else -> Unit
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterForBroadcastMessages()
        view!!.leave()
        view!!.dispose()
        view = null
        finishAndRemoveTask() //  khi out meeting
        JitsiMeetActivityDelegate.onHostDestroy(this.parent)


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        JitsiMeetActivityDelegate.onNewIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        JitsiMeetActivityDelegate.onHostResume(this)
    }

    override fun onStop() {
        super.onStop()
        JitsiMeetActivityDelegate.onHostPause(this.parent)
    }


    private fun handleLeaveConference() {
        view?.leave()
    }
    override fun onBackPressed() {
        JitsiMeetActivityDelegate.onBackPressed()
        super.onBackPressed()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
            view?.enterPictureInPicture()
        }
    }

    private fun onConferenceTerminated(data: Map<String, Any>) {
        Timber.v("JitsiMeetViewListener.onConferenceTerminated()")
        // Do not finish if there is an error
        if (data["error"] == null) {
            conferenceViewModel.handle(ConferenceViewActions.OnConferenceLeft)
        }
    }

    override fun create(initialState: ConferenceViewState): ConferenceViewModel {
        return conferenceViewModelFactory.create(initialState)
    }


    private fun setupToolbar() {
        views.toolbar.also {
            title = ""
            setSupportActionBar(it)
        }
        views.toolbarTitle.text = getString(R.string.service_rating)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    fun updateToolbar(toolbarVisible:Boolean,videocallVisible:Boolean) {
        if (toolbarVisible == false)
            views.toolbar.visibility = View.GONE
        else
            views.toolbar.visibility = View.VISIBLE

        if(videocallVisible == false) {
            views.jitsiLayout.visibility = View.GONE

        }
        else {
            views.jitsiLayout.visibility = View.VISIBLE

        }


    }
}
