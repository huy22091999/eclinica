/*
 * Copyright 2019 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package im.vector.app.features.workers.signout

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import im.vector.app.R
import im.vector.app.core.extensions.cannotLogoutSafely
import im.vector.app.core.extensions.vectorComponent
import im.vector.app.ext.registration.service.MyService
import im.vector.app.features.MainActivity
import im.vector.app.features.MainActivityArgs

class SignOutUiWorker(private val activity: FragmentActivity) {

    fun perform() {
        val session = activity.vectorComponent().activeSessionHolder().getSafeActiveSession() ?: return
        if (session.cannotLogoutSafely()) {
            // The backup check on logout flow has to be displayed if there are keys in the store, and the keys backup state is not Ready
            val signOutDialog = SignOutBottomSheetDialogFragment.newInstance()
            signOutDialog.onSignOut = Runnable {
                doSignOut()
            }
            signOutDialog.show(activity.supportFragmentManager, "SO")
        } else {
            // Display a simple confirmation dialog
            AlertDialog.Builder(activity)
                    .setTitle(R.string.action_sign_out)
                    .setMessage(R.string.action_sign_out_confirmation_simple)
                    .setPositiveButton(R.string.action_sign_out) { _, _ ->
                        doSignOut()
                        val serviceIntent = Intent(activity, MyService::class.java)
                        activity.stopService(serviceIntent)
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
        }
    }

    private fun doSignOut() {
        MainActivity.restartApp(activity, MainActivityArgs(clearCredentials = true))
    }
}
