/*
 * Copyright (c) 2020 New Vector Ltd
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

package im.vector.app.ext.conference

import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.ext.data.model.MedicalExamination
import im.vector.app.ext.registration.RegistrationViewEvents
import org.jitsi.meet.sdk.JitsiMeetUserInfo

sealed class ConferenceViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : ConferenceViewEvents()
    data class Failure(val throwable: Throwable) : ConferenceViewEvents()
    data class EligibleCustomerComplete(val medicalExamination: MedicalExamination) : ConferenceViewEvents()

    object LaunchServiceRatingFragment: ConferenceViewEvents()
    object SaveServiceRatingComplete: ConferenceViewEvents()
    object LeaveConference : ConferenceViewEvents()
    object Finish : ConferenceViewEvents()

}
