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

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.extensions.exhaustive
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.model.ServiceEvaluationCardByEncounter
import im.vector.app.ext.data.repository.EncounterRepository
import im.vector.app.ext.data.repository.PatientRepository
import im.vector.app.ext.registration.RegistrationActions
import im.vector.app.ext.registration.RegistrationViewEvents
import im.vector.app.ext.registration.RegistrationViewModel
import im.vector.app.ext.registration.RegistrationViewState
import im.vector.app.features.call.conference.JitsiCallViewActions
import im.vector.app.features.themes.ThemeProvider
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.widgets.model.Widget
import org.matrix.android.sdk.api.session.widgets.model.WidgetType
import org.matrix.android.sdk.api.util.toMatrixItem
import org.matrix.android.sdk.rx.asObservable
import java.net.URL

class ConferenceViewModel @AssistedInject constructor(
    @Assisted initialState: ConferenceViewState,
    private val patientRepository: PatientRepository,
    private val encounterRepository: EncounterRepository,
    ) : VectorViewModel<ConferenceViewState, ConferenceViewActions, ConferenceViewEvents>(initialState) {

    @AssistedFactory
    interface Factory {
        fun create(initialState: ConferenceViewState): ConferenceViewModel
    }

    companion object : MvRxViewModelFactory<ConferenceViewModel, ConferenceViewState> {

        override fun initialState(viewModelContext: ViewModelContext): ConferenceViewState {
            return ConferenceViewState(
                asyncPatientInfo = Uninitialized,
                asyncMedicalExamination = Uninitialized,
                asyncServiceRating = Uninitialized,
            )
        }

        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: ConferenceViewState): ConferenceViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    override fun handle(action: ConferenceViewActions) {
        when (action) {
            is ConferenceViewActions.OnConferenceLeft -> {
                // Quit
                _viewEvents.post(ConferenceViewEvents.Finish)
            }
            is ConferenceViewActions.QueryPatientInfoData -> handleQueryPatientInfoData()
            is ConferenceViewActions.QueryMedicalExaminationById -> handleQueryEncounterById(action.encounterId)
            is ConferenceViewActions.EligibleCustomer -> handleEligibleCustomer(action.encounterId)
            is ConferenceViewActions.ServiceRating -> {
                _viewEvents.post(ConferenceViewEvents.LaunchServiceRatingFragment)
            }
            is ConferenceViewActions.QueryServiceRating -> handleQueryServiceRating(action.patientId, action.encounterId)
            is ConferenceViewActions.SaveServiceRating -> handleSaveServiceRating(action.serviceRatingForm)
        }
    }

    private fun handleQueryPatientInfoData() {
        setState {
            copy(asyncPatientInfo = Loading())
        }
        patientRepository.getPatientInfo().execute {
            copy(asyncPatientInfo = it)
        }
    }

    private fun handleQueryEncounterById(encounterId: String) {
        setState {
            copy(asyncMedicalExamination = Loading())
        }
        encounterRepository.getMedicalExaminationId(encounterId).execute {
            copy(asyncMedicalExamination = it)
        }
    }

    private fun handleEligibleCustomer(encounterId: String) {
        setState {
            copy(asyncMedicalExamination = Loading())
        }

        encounterRepository.patientAgree(encounterId).execute {
            asyncMedicalExamination.invoke()?.let { it1 ->
                _viewEvents.post(ConferenceViewEvents.EligibleCustomerComplete(it1))
            }
            copy(asyncMedicalExamination = it)
        }
    }

    private fun handleQueryServiceRating(patientId: String, encounterId: String) {
        setState {
            copy(asyncServiceRating = Loading())
        }
        encounterRepository.getServiceRating(patientId, encounterId).execute {
            copy(asyncServiceRating = it)
        }
    }

    private fun handleSaveServiceRating(serviceRatingForm: ServiceEvaluationCardByEncounter) {
        setState {
            copy(asyncEditingServiceRating = Loading())
        }

        encounterRepository.saveServiceRating(serviceRatingForm, serviceRatingForm.id).execute {
            _viewEvents.post(ConferenceViewEvents.SaveServiceRatingComplete)
            copy(asyncEditingServiceRating = it)
        }
    }


}
