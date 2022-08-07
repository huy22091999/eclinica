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

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.airbnb.mvrx.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.FragmentLoginResetPasswordBinding
import im.vector.app.ext.enrollment.ClientEnrollmentViewModel
import im.vector.app.ext.utils.clickWithThrottle
import im.vector.app.ext.utils.isNullOrEmptyAlt
import im.vector.app.ext.utils.toStringAlt


import javax.inject.Inject

/**
 * In this screen, the user is asked for email and new password to reset his password
 */
class LoginResetPasswordFragment @Inject constructor() :
    VectorBaseFragment<FragmentLoginResetPasswordBinding>() {

    private val viewModel: ClientEnrollmentViewModel by activityViewModel()
    private val loginViewModel: LoginViewModel by activityViewModel()
    var displayName:String?=null
    var email:String?=null
    var isNumber:String?=null
    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentLoginResetPasswordBinding {
        return FragmentLoginResetPasswordBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSubmitButton()
        views.registerSubtitle.clickWithThrottle {
            loginViewModel.handle(LoginAction.ReturnToLogin)
        }
        viewModel.observeViewEvents {
            handleViewEvents(it)
        }
    }

    private fun handleViewEvents(it: LoginViewEvents) {
        when (it) {
            is LoginViewEvents.ResetPasswordSendEmail -> {
                withState(viewModel) { state ->
                    when (state.asyncSendEmail) {
                        is Success -> {
                            AlertDialog.Builder(requireActivity())
                                .setTitle(R.string.login_reset_password_warning_title)
                                .setMessage(R.string.email_sent)
                                .setPositiveButton(R.string.return_login) { _, _ ->
                                    viewModel.resetStateResetPassword()
                                    loginViewModel.handle(LoginAction.ReturnToLogin)
                                }
                                .setNegativeButton(R.string.cancel, null)
                                .show()
                        }
                        else -> {}
                    }
                }
            }
            is LoginViewEvents.ResetPasswordCheckUserExist -> {
                withState(viewModel) { state ->
                    when (state.asyncCheckUserExist) {
                        is Success -> {
                            state.asyncCheckUserExist.invoke().let { b ->
                                if (b) {
                                    var displayName = views.fullname.text.toString()
                                    var email = views.resetPasswordEmail.text.toString()
                                    var isNumber = views.nationalId.text.toString()
                                    AlertDialog.Builder(requireActivity())
                                        .setTitle(R.string.login_reset_password_warning_title)
                                        .setMessage(R.string.login_reset_password_warning_content)
                                        .setPositiveButton(R.string.login_reset_password_warning_submit) { _, _ ->
                                            viewModel.handle(
                                                LoginAction.SendEmailResetPassword(
                                                    email,
                                                    displayName,
                                                    isNumber
                                                )
                                            )
                                        }
                                        .setNegativeButton(R.string.cancel, null)
                                        .show()
                                } else {
                                    AlertDialog.Builder(requireActivity())
                                        .setTitle(R.string.error)
                                        .setMessage(R.string.not_found_account)
                                        .setNegativeButton(R.string.ok, null)
                                        .show()
                                }


                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    private fun setupSubmitButton() {
        views.resetPasswordSubmit.setOnClickListener { submit() }
    }

    private fun submit() {
        if (validate()) {
            viewModel.handle(LoginAction.CheckUserExist(email, displayName, isNumber))
        }

    }

    private fun validate(): Boolean {
        validateField(textField = views.resetPasswordEmail, true, -1, views.resetPasswordEmailTil)
        validateField(views.fullname, true, -1, views.fullnameTil)
        validateField(views.nationalId, true, -1, views.nationalIdTil)
        displayName = views.fullname.text.toString()
        email = views.resetPasswordEmail.text.toString()
        isNumber = views.nationalId.text.toString()
        if(displayName.isNullOrBlank()||email.isNullOrBlank()||isNumber.isNullOrBlank())
            return false
        if (!isEmailValid(views.resetPasswordEmail.toStringAlt().trim())) {
            getString(R.string.validate_email).also { views.resetPasswordEmailTil.error = it }
            return false
        }
        return true
    }

    private fun isEmailValid(email: String?): Boolean {
        return !(email == null || TextUtils.isEmpty(email)) && Patterns.EMAIL_ADDRESS.matcher(email)
            .matches()
    }

    private fun validateField(
        textField: TextInputEditText,
        required: Boolean,
        min: Int,
        text: TextInputLayout
    ) {
        if (required && textField.toStringAlt().isNullOrEmptyAlt()) {
            getString(R.string.msg_required_field).also {
                text.error = it
            }
            textField.requestFocus()
        }
        if (min != -1 && (!textField.toStringAlt()
                .isNullOrEmptyAlt() && textField.toStringAlt().length < min)
        ) {
            getString(R.string.msg_validate_min_field).also {
                text.error = it

            }
            textField.requestFocus()
        }

        textField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text.error = null
            }
        })
    }



}
