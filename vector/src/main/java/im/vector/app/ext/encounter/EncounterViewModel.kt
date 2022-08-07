package im.vector.app.ext.encounter

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.repository.AppointmentRepository
import im.vector.app.ext.data.repository.EncounterRepository
import im.vector.app.ext.data.repository.PatientRepository
import kotlinx.coroutines.launch

class EncounterViewModel @AssistedInject constructor(
    @Assisted initialState: EncounterViewState,
    val appointmentRepos: AppointmentRepository,
    val patientRepository: PatientRepository,
    val encounterRepository: EncounterRepository,
) : VectorViewModel<EncounterViewState, EncounterActions, EncounterViewEvents>(initialState) {

    @AssistedFactory
    interface Factory {
        fun create(initialState: EncounterViewState): EncounterViewModel
    }

    companion object : MvRxViewModelFactory<EncounterViewModel, EncounterViewState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: EncounterViewState): EncounterViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    override fun handle(action: EncounterActions) {
        when (action) {
            is EncounterActions.QueryPatientInfoData -> handleQueryPatientInfo()
            is EncounterActions.QueryTreatmentPeriodByPatientId -> handleQueryTreatmentPeriodByPatientId(action.patientId)
            is EncounterActions.LaunchEncounterDetailFragment -> {
                _viewEvents.post(EncounterViewEvents.LaunchDetailFragment)
            }
            is EncounterActions.QueryMedicalExaminationById -> handleQueryMedicalExaminationById(action.encounterId)
        }
    }

    private fun handleQueryPatientInfo() = viewModelScope.launch  {
        setState {
            copy(asyncPatientInfo = Loading())
        }
        patientRepository.getPatientInfo().execute {
            copy(asyncPatientInfo = it)
        }
    }

    private fun handleQueryTreatmentPeriodByPatientId(patientId: String) = viewModelScope.launch  {
        setState {
            copy(asyncListTreatmentPeriod = Loading())
        }
        encounterRepository.getTreatmentPeriodByPatientId(patientId).execute {
            copy(asyncListTreatmentPeriod = it)
        }

    }



    private fun handleQueryMedicalExaminationById(encounterId: String) = viewModelScope.launch  {
        setState {
            copy(asyncMedicalExamination = Loading())
        }
        encounterRepository.getMedicalExaminationId(encounterId).execute {
            copy(asyncMedicalExamination = it)
        }
    }

}
