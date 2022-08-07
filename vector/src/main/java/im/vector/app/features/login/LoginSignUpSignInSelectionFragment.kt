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

package im.vector.app.features.login

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.HandlerCompat
import androidx.core.view.isVisible
import com.airbnb.mvrx.withState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.vector.app.R
import im.vector.app.core.extensions.addFragmentToBackstack
import im.vector.app.core.extensions.toReducedUrl
import im.vector.app.databinding.FragmentLoginSignupSigninSelectionBinding
import org.matrix.android.sdk.api.failure.Failure
import timber.log.Timber
import java.net.UnknownHostException

import javax.inject.Inject

/**
 * In this screen, the user is asked to sign up or to sign in to the homeserver
 */
class LoginSignUpSignInSelectionFragment @Inject constructor() :
    AbstractSSOLoginFragment<FragmentLoginSignupSigninSelectionBinding>() {

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginSignupSigninSelectionBinding {
        return FragmentLoginSignupSigninSelectionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }

    private fun setupViews() {
        views.loginSignupSigninSubmit.setOnClickListener { submit() }
        views.loginSignupSigninSignIn.setOnClickListener { signIn() }
    }


    private fun setupButtons(state: LoginViewState) {
        when (state.loginMode) {
            is LoginMode.Sso -> {
                // change to only one button that is sign in with sso
                views.loginSignupSigninSubmit.text = getString(R.string.login_signin_sso)
                views.loginSignupSigninSignIn.isVisible = false
            }
            else -> {
                views.loginTitle.text = getString(R.string.TelePrEP)
                views.loginText.text = getString(R.string.welcome_text)
                views.loginSignupSigninSubmit.text = getString(R.string.login_signup)
                views.loginSignupSigninSignIn.isVisible = true
            }
        }
    }

    private fun submit() {
        loginViewModel.handle(LoginAction.EnrollClient)
    }

    private fun signIn() {
        loginViewModel.handle(LoginAction.UpdateSignMode(SignMode.SignIn))
    }

    // Begin Globits01 edit
    override fun onError(throwable: Throwable) {
        val error = if (throwable is Failure.NetworkConnection
            && throwable.ioException is UnknownHostException
        ) {
            // Invalid homeserver?
            getString(R.string.login_error_homeserver_not_found)
        } else {
            errorFormatter.toHumanReadable(throwable)
        }

        AlertDialog.Builder(activity as Context)
            .setCancelable(false)
            .setTitle(R.string.app_name)
            .setMessage(error)
            .setNeutralButton(R.string.action_return) { _, _ ->
                parentFragmentManager.popBackStack()
            }.show()
    }
    // End edit

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction.ResetSignMode)
    }

    override fun updateWithState(state: LoginViewState) {
//        setupUi(state)
        setupButtons(state)
    }
}
