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

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.PersistState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import im.vector.app.ext.data.model.Clinic
import im.vector.app.ext.data.model.Policies
import im.vector.app.ext.enrollment.CheckDuplicateResponse

data class LoginViewState(
    val asyncLoginAction: Async<Unit> = Uninitialized,
    val asyncHomeServerLoginFlowRequest: Async<Unit> = Uninitialized,
    val asyncResetPassword: Async<Unit> = Uninitialized,
    val asyncResetMailConfirmed: Async<Unit> = Uninitialized,
    val asyncRegistration: Async<Unit> = Uninitialized, //
    val asyncPreRegistration: Async<CheckDuplicateResponse> = Uninitialized, // to check username duplicate
    val isSubmitResgister: Boolean = false,
    val asyncPolicies: Async<List<Policies>> = Uninitialized, // test, maybe delete
    val asyncCheckUserExist: Async<Boolean> = Uninitialized,
    val asyncSendEmail: Async<Unit> = Uninitialized,
    val asyncClinics: Async<List<Clinic>> = Uninitialized,
// User choices
    @PersistState
    val serverType: ServerType = ServerType.Unknown,

    @PersistState
    val signMode: SignMode = SignMode.Unknown,

    @PersistState
    val resetPasswordEmail: String? = null,

    @PersistState
    val homeServerUrl: String? = null,

// For SSO session recovery
    @PersistState
    val deviceId: String? = null,

// Network result
    @PersistState
    val loginMode: LoginMode = LoginMode.Unknown,

    @PersistState
// Supported types for the login. We cannot use a sealed class for LoginType because it is not serializable
    val loginModeSupportedTypes: List<String> = emptyList(),
    val knownCustomHomeServersUrls: List<String> = emptyList()
) : MvRxState {

    fun isLoading(): Boolean {
        return asyncLoginAction is Loading
                || asyncHomeServerLoginFlowRequest is Loading
                || asyncResetPassword is Loading
                || asyncResetMailConfirmed is Loading
                || asyncRegistration is Loading
                || asyncPreRegistration is Loading
                // Keep loading when it is success because of the delay to switch to the next Activity
                || asyncLoginAction is Success
                || asyncCheckUserExist is Loading
                || asyncSendEmail is Loading
                || asyncClinics is Loading
    }

    fun isUserLogged(): Boolean {
        return asyncLoginAction is Success
    }


}
