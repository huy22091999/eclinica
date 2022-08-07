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

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.ext.data.model.ServiceEvaluationCardByEncounter
import im.vector.app.ext.registration.RegistrationActions

sealed class ConferenceViewActions : VectorViewModelAction {

    /**
     * The ViewModel will either ask the View to finish, or to join another conf.
     */
    data class EligibleCustomer(val encounterId: String) : ConferenceViewActions()
    data class QueryMedicalExaminationById(val encounterId: String): ConferenceViewActions()
    data class QueryServiceRating(val patientId: String, val encounterId: String) : ConferenceViewActions()
    data class SaveServiceRating(val serviceRatingForm: ServiceEvaluationCardByEncounter) : ConferenceViewActions()

    object OnConferenceLeft: ConferenceViewActions()
    object QueryPatientInfoData : ConferenceViewActions()
    object ServiceRating : ConferenceViewActions()
}
