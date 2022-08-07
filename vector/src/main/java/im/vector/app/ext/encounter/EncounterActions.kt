package im.vector.app.ext.encounter

import im.vector.app.core.platform.VectorViewModelAction

sealed class EncounterActions : VectorViewModelAction {
    data class QueryTreatmentPeriodByPatientId(val patientId: String) : EncounterActions()
    data class QueryMedicalExaminationById(val encounterId: String) : EncounterActions()

    object QueryPatientInfoData : EncounterActions()
    object LaunchEncounterDetailFragment : EncounterActions()

}
