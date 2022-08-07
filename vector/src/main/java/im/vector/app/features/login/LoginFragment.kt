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

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.autofill.HintConstants
import androidx.core.text.isDigitsOnly
import androidx.core.view.isVisible
import com.airbnb.mvrx.*
import com.jakewharton.rxbinding3.widget.textChanges
import im.vector.app.R
import im.vector.app.R.*
import im.vector.app.core.extensions.*
import im.vector.app.databinding.FragmentLoginBinding
import im.vector.app.ext.data.model.Clinic
import im.vector.app.ext.enrollment.ClientEnrollmentViewModel
import im.vector.app.ext.utils.clickWithThrottle
import io.reactivex.Observable
import io.reactivex.rxkotlin.subscribeBy
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.failure.isInvalidPassword
import javax.inject.Inject

/**
 * In this screen:
 * In signin mode:
 * - the user is asked for login (or email) and password to sign in to a homeserver.
 * - He also can reset his password
 * In signup mode:
 * - the user is asked for login and password
 */
class LoginFragment @Inject constructor() : AbstractSSOLoginFragment<FragmentLoginBinding>() {
    private val viewModel: ClientEnrollmentViewModel by activityViewModel()
    private var passwordShown = false
    private var isSignupMode = false
    private lateinit var clinicsAdapter: ArrayAdapter<Clinic>
    private val clinics = mutableListOf<Clinic>()

    // Temporary patch for https://github.com/vector-im/riotX-android/issues/1410,
    // waiting for https://github.com/matrix-org/synapse/issues/7576
    private var isNumericOnlyUserIdForbidden = false

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentLoginBinding {
        return FragmentLoginBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeViewEvents {
            handleEvents(it)
        }
        setupSubmitButton()
        setupForgottenPasswordButton()
        setupPasswordReveal()
        setUpChooseClinic()
        getClinics()
        views.passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                submit()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    fun handleEvents(events: LoginViewEvents) {
        when (events) {
            is LoginViewEvents.GetClinicsSuccess -> {
                withState(viewModel) { state ->
                    state.asyncClinics.invoke().let {
                        clinics.clear()
                        clinics.add(
                            Clinic(
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                null,
                                getString(R.string.let_choose_clinic)
                            )
                        )
                        if (it != null) {
                            clinics.addAll(it)
                        }
                        clinicsAdapter.notifyDataSetChanged()

                    }
                }
            }
            else -> Unit
        }
    }

    private fun getClinics() {
        viewModel.handle(LoginAction.GetClinics)
    }


    override fun onResume() {
        cleanupUi()
        super.onResume()
    }

    private fun setupForgottenPasswordButton() {
        views.forgetPasswordButton.setOnClickListener { forgetPasswordClicked() }
    }

    private fun setupAutoFill(state: LoginViewState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            when (state.signMode) {
                SignMode.Unknown -> error("developer error")
                SignMode.SignUp -> {
                    views.loginField.setAutofillHints(HintConstants.AUTOFILL_HINT_NEW_USERNAME)
                    views.passwordField.setAutofillHints(HintConstants.AUTOFILL_HINT_NEW_PASSWORD)
                }
                SignMode.SignIn,
                SignMode.SignInWithMatrixId -> {
                    views.loginField.setAutofillHints(HintConstants.AUTOFILL_HINT_USERNAME)
                    views.passwordField.setAutofillHints(HintConstants.AUTOFILL_HINT_PASSWORD)
                }
            }.exhaustive
        }
    }

    fun setUpChooseClinic() {
        clinicsAdapter =
            ArrayAdapter(
                requireContext(),
                layout._globits_simple_list_item2,
                clinics
            )
    }

    @SuppressLint("ResourceType")
    private fun submit() {
        cleanupUi()

        val login = views.loginField.text.toString()
        val password = views.passwordField.text.toString()

        var error = 0
        if (login.isEmpty()) {
            views.loginFieldTil.error = getString(
                if (isSignupMode) {
                    string.error_empty_field_choose_user_name
                } else {
                    string.error_empty_field_enter_user_name
                }
            )
            error++
        }
        if (isSignupMode && isNumericOnlyUserIdForbidden ) {
            views.loginFieldTil.error = "The homeserver does not accept username with only digits."
            error++
        }
        if (password.isEmpty()) {
            views.passwordFieldTil.error = getString(
                if (isSignupMode) {
                    string.error_empty_field_choose_password
                } else {
                    string.error_empty_field_your_password
                }
            )
            error++
        }

        if (error == 0) {
            loginViewModel.handle(
                LoginAction.LoginOrRegister(
                    login,
                    password,
                    "https://telehealth.globits.net:8052/telehealth",
                    getString(string.login_default_session_public_name)
                )
            )

        }
    }

    private fun cleanupUi() {
        views.loginSubmit.hideKeyboard()
        views.loginFieldTil.error = null
        views.passwordFieldTil.error = null
    }

    private fun setupUi(state: LoginViewState) {

        views.loginFieldTil.hint = getString(
            when (state.signMode) {
                SignMode.Unknown -> error("developer error")
                SignMode.SignUp -> string.login_signup_username_hint
                SignMode.SignIn -> string.login_signin_username_hint
                SignMode.SignInWithMatrixId -> string.login_signin_matrix_id_hint
            }
        )

        // Handle direct signin first
        if (state.signMode == SignMode.SignInWithMatrixId) {
//            views.loginServerIcon.isVisible = false
            views.loginTitle.text = getString(string.login_signin_matrix_id_title)
            views.loginNotice.text = getString(string.login_signin_matrix_id_notice)
//            views.loginPasswordNotice.isVisible = true
        } else {
            val resId = when (state.signMode) {
                SignMode.Unknown -> error("developer error")
                SignMode.SignUp -> string.login_signup_to
                SignMode.SignIn -> string.login_connect_to
                SignMode.SignInWithMatrixId -> string.login_connect_to
            }

            when (state.serverType) {
                ServerType.MatrixOrg -> {
//                    views.loginServerIcon.isVisible = true
//                    views.loginServerIcon.setImageResource(R.drawable.ic_logo_matrix_org)
                    views.loginTitle.text = getString(resId, state.homeServerUrl.toReducedUrl())
                    views.loginNotice.text = getString(string.login_server_matrix_org_text)
                }
                ServerType.EMS -> {
//                    views.loginServerIcon.isVisible = true
//                    views.loginServerIcon.setImageResource(R.drawable.ic_logo_element_matrix_services)
                    views.loginTitle.text = getString(resId, "Element Matrix Services")
                    views.loginNotice.text = getString(string.login_server_modular_text)
                }

                ServerType.Other -> {
                    if (isSignupMode) {
                        views.loginTitle.text = getString(string.login_signup)
                        views.loginNotice.isVisible = true
                        views.loginNotice.visibility = View.VISIBLE
                        views.loginNotice.text = getString(string.login_signup_server_gcom_text)
                    } else {
                        views.loginNotice.isVisible = true
                        views.loginNotice.visibility = View.VISIBLE
                        views.loginTitle.text = getString(string.login_signin)
                    }
                }
                // end edit
                ServerType.Unknown -> Unit /* Should not happen */
            }
        }

        views.contactBusiness.clickWithThrottle {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:0347826167")
            startActivity(intent)
        }

        views.contactTechnical.clickWithThrottle {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:01234567890")
            startActivity(intent)
        }
    }

    private fun setupButtons(state: LoginViewState) {
//        views.forgetPasswordButton.isVisible = false // state.signMode == SignMode.SignIn
//        views.forgetPasswordButton.visibility = View.GONE
        views.loginSubmit.text = getString(
            when (state.signMode) {
                SignMode.Unknown -> error("developer error")
                SignMode.SignUp -> string.login_signup_submit
                SignMode.SignIn,
                SignMode.SignInWithMatrixId -> string.login_signin
            }
        )

        views.loginNotice.clickWithThrottle {
            loginViewModel.handle(LoginAction.EnrollClient)
        }
    }

    private fun setupSubmitButton() {
        views.loginSubmit.setOnClickListener { submit() }
        Observable
            .combineLatest(
                views.loginField.textChanges().map { it.trim().isNotEmpty() },
                views.passwordField.textChanges().map { it.isNotEmpty() }
            ) { isLoginNotEmpty, isPasswordNotEmpty ->
                isLoginNotEmpty && isPasswordNotEmpty
            }
            .subscribeBy {
                views.loginFieldTil.error = null
                views.passwordFieldTil.error = null
                views.loginSubmit.isEnabled = it
            }
            .disposeOnDestroyView()
    }

    private fun forgetPasswordClicked() {
        loginViewModel.handle(LoginAction.PostViewEvent(LoginViewEvents.OnForgetPasswordClicked))
    }

    private fun setupPasswordReveal() {
        passwordShown = false

        views.passwordReveal.setOnClickListener {
            passwordShown = !passwordShown

            renderPasswordField()
        }

        renderPasswordField()
    }

    private fun renderPasswordField() {
        views.passwordField.showPassword(passwordShown)
        views.passwordReveal.render(passwordShown)
    }

    override fun resetViewModel() {
        loginViewModel.handle(LoginAction.ResetLogin)
    }

    override fun onError(throwable: Throwable) {
        // Show M_WEAK_PASSWORD error in the password field
        if (throwable is Failure.ServerError
            && throwable.error.code == MatrixError.M_WEAK_PASSWORD
        ) {
            views.passwordFieldTil.error = errorFormatter.toHumanReadable(throwable)
        } else {
            views.loginFieldTil.error = errorFormatter.toHumanReadable(throwable)
        }
    }


    override fun updateWithState(state: LoginViewState) {
        isSignupMode = state.signMode == SignMode.SignUp
        isNumericOnlyUserIdForbidden = state.serverType == ServerType.MatrixOrg
        setupUi(state)
        setupAutoFill(state)
        setupButtons(state)
        when (state.asyncLoginAction) {
            is Loading -> {
                // Ensure password is hidden
                passwordShown = false
                renderPasswordField()
            }
            is Fail -> {
                val error = state.asyncLoginAction.error
                if (error is Failure.ServerError
                    && error.error.code == MatrixError.M_FORBIDDEN
                    && error.error.message.isEmpty()
                ) {
                    // Login with email, but email unknown
                    views.loginFieldTil.error = getString(string.login_login_with_email_error)
                } else {
                    // Trick to display the error without text.
                    views.loginFieldTil.error = " "
                    val errorMsg = errorFormatter.toHumanReadable(error)
                    val invalidGrant = errorMsg.contains("invalid_grant", true)
                    val badGateway = errorMsg.contains("502 Bad Gateway", true)

                    if ((error.isInvalidPassword() && spaceInPassword()) || invalidGrant || badGateway) {
                        if (invalidGrant) {
                            views.passwordFieldTil.error =
                                getString(string.auth_invalid_login_param)
                        } else if (badGateway) {
                            views.passwordFieldTil.error =
                                getString(string.auth_server_error)
                        } else {
                            views.passwordFieldTil.error =
                                getString(string.auth_invalid_login_param_space_in_password)
                        }
                    } else {
                        views.passwordFieldTil.error = errorMsg
                    }
                }
            }
            // Success is handled by the LoginActivity
            is Success -> {
                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_LONG).show()
            }

            else -> {}
        }

        when (state.asyncRegistration) {
            is Loading -> {
                // Ensure password is hidden
                passwordShown = false
                renderPasswordField()
            }
            // Success is handled by the LoginActivity
            else -> Unit
        }
    }


    /**
     * Detect if password ends or starts with spaces
     */
    private fun spaceInPassword() = views.passwordField.text.toString().let { it.trim() != it }
}
