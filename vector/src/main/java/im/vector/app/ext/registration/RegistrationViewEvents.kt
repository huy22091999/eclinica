package im.vector.app.ext.registration

import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.AddressType

sealed class RegistrationViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : RegistrationViewEvents()
    data class Failure(val throwable: Throwable) : RegistrationViewEvents()

    data class ApplyNewDistricts(val addressType: AddressType, val clearCommune: Boolean? = true) :
        RegistrationViewEvents()

    data class ApplyNewCommunes(val addressType: AddressType) : RegistrationViewEvents()

    data class SavePatientComplete(val patient: Patient) : RegistrationViewEvents()

    data class SaveSelectTestServiceComplete(val labTestOrder: LabTestOrder) : RegistrationViewEvents()

    data class SaveImageComplete(val attachfile : PatientMedicalRecordFile) : RegistrationViewEvents()

    data class SaveAppointmentComplete(val editingAppointment: Appointment) : RegistrationViewEvents()

//    object SaveImageComplete : RegistrationViewEvents()

    data class EligibleCustomerComplete(val medicalExamination: MedicalExamination) : RegistrationViewEvents()

    object ApplyNewHealthOrg : RegistrationViewEvents()
    object LaunchProfileEditFragment : RegistrationViewEvents()
    object LaunchPrepRegistrationFragment : RegistrationViewEvents()
    object LaunchLabTestSelectionFragment : RegistrationViewEvents()
    object LaunchLabTestResultFragment : RegistrationViewEvents()
    object LaunchAppointmentSetupFragment : RegistrationViewEvents()
    object SaveScreeningFormComplete : RegistrationViewEvents()
    object LaunchMedicalExaminationFragment : RegistrationViewEvents()
//    object SaveAppointmentComplete : RegistrationViewEvents()
    object LaunchServiceRatingFragment: RegistrationViewEvents()
    object SaveServiceRatingComplete: RegistrationViewEvents()
    object CheckSupportOnlineMessage:RegistrationViewEvents()
    object QueryAppointment:RegistrationViewEvents()
    object GetEncounterSlot:RegistrationViewEvents()
    object CheckDuplicate:RegistrationViewEvents()
}
