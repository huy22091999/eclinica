package im.vector.app.ext.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.core.ui.views.CurrentCallsView
import im.vector.app.core.utils.registerForPermissionsResult
import im.vector.app.core.utils.toast
import im.vector.app.databinding.FragmentTestCallBinding
import im.vector.app.features.call.VectorCallActivity
import im.vector.app.features.call.webrtc.WebRtcCallManager
import im.vector.app.features.home.room.detail.*
import im.vector.app.features.settings.VectorPreferences
import javax.inject.Inject

class TestCallFragment @Inject constructor(
    private val vectorPreferences: VectorPreferences,
    val roomDetailViewModelFactory: RoomDetailViewModel.Factory,
    private val callManager: WebRtcCallManager
) :
    VectorBaseFragment<FragmentTestCallBinding>(),
    CurrentCallsView.Callback {

    private val roomDetailArgs: RoomDetailArgs by args()
    private val roomDetailViewModel: RoomDetailViewModel by fragmentViewModel()

    private lateinit var callActionsHandler: StartCallActionsHandler

    private val startCallActivityResultLauncher = registerForPermissionsResult { allGranted ->
        if (allGranted) {
            (roomDetailViewModel.pendingAction as? RoomDetailAction.StartCall)?.let {
                roomDetailViewModel.pendingAction = null
                roomDetailViewModel.handle(it)
            }
        } else {
            context?.toast(R.string.permissions_action_not_performed_missing_permissions)
            cleanUpAfterPermissionNotGranted()
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTestCallBinding {
        return FragmentTestCallBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callActionsHandler = StartCallActionsHandler(
            roomId = roomDetailArgs.roomId,
            fragment = this,
            vectorPreferences = vectorPreferences,
            roomDetailViewModel = roomDetailViewModel,
            callManager = callManager,
            startCallActivityResultLauncher = startCallActivityResultLauncher,
            showDialogWithMessage = ::showDialogWithMessage,
            onTapToReturnToCall = ::onTapToReturnToCall
        ).register()

        roomDetailViewModel.observeViewEvents {
            when (it) {
                is RoomDetailViewEvents.Failure -> showErrorInSnackbar(it.throwable)
                is RoomDetailViewEvents.ShowWaitingView -> vectorBaseActivity.showWaitingView()
                is RoomDetailViewEvents.HideWaitingView -> vectorBaseActivity.hideWaitingView()
                is RoomDetailViewEvents.DisplayAndAcceptCall -> acceptIncomingCall(it)
                else -> Unit
            }
        }

        handleButtonClicks()
    }

    override fun onDestroy() {
        roomDetailViewModel.handle(RoomDetailAction.ExitTrackingUnreadMessagesState)
        super.onDestroy()
    }

    override fun onTapToReturnToCall() {
        callManager.getCurrentCall()?.let { call ->
            VectorCallActivity.newIntent(
                context = requireContext(),
                callId = call.callId,
                roomId = call.roomId,
                otherUserId = call.mxCall.opponentUserId,
                isIncomingCall = !call.mxCall.isOutgoing,
                isVideoCall = call.mxCall.isVideoCall,
                mode = null
            ).let {
                startActivity(it)
            }
        }
    }

    // private methods
    private fun handleButtonClicks() {
        views.voiceCall.debouncedClicks {
            callActionsHandler.onVoiceCallClicked()
        }

        views.videoCall.debouncedClicks {
            callActionsHandler.onVideoCallClicked()
        }

        views.chat.debouncedClicks {
            (activity as TestCallActivity).startChatActivity()
//            (activity as TestCallActivity).showChatDialog()
//            (activity as TestCallActivity).replaceFragment(RoomDetailFragment::class.java)
        }
    }

    private fun acceptIncomingCall(event: RoomDetailViewEvents.DisplayAndAcceptCall) {
        val intent = VectorCallActivity.newIntent(
            context = vectorBaseActivity,
            mxCall = event.call.mxCall,
            mode = VectorCallActivity.INCOMING_ACCEPT
        )
        startActivity(intent)
    }

    private fun showDialogWithMessage(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton(getString(R.string.ok), null)
            .show()
    }

    private fun cleanUpAfterPermissionNotGranted() {
        // Reset all pending data
        roomDetailViewModel.pendingAction = null
    }

}
