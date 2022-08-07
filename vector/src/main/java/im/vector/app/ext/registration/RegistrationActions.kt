package im.vector.app.ext.registration

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.AddressType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

sealed class RegistrationActions : VectorViewModelAction {

    data class QueryPatientData(val patientId: String) : RegistrationActions()
    data class ProvinceSelected(
        val id: String,
        val addressType: AddressType,
        val clearCommune: Boolean? = true
    ) :
        RegistrationActions()

    data class ProvinceHealthOrgSelected(val code: String?) : RegistrationActions()
    data class DistrictSelected(val id: String, val addressType: AddressType) :
        RegistrationActions()

    data class UpdatePatientData(val editingPatient: Patient) : RegistrationActions()
    data class SaveEditingPatient(val editingPatient: Patient) : RegistrationActions()
    data class QueryPeriodPrepScreen(val screeningId: String?) : RegistrationActions()
    data class SaveScreeningResult(val screeningForm: PatientPeriodPrepScreen) :
        RegistrationActions()

    data class UploadLabTestResultFile(val fileBody: MultipartBody.Part) : RegistrationActions()
    data class GenerateEncounterSlot(val currentDate: Date) : RegistrationActions()
    data class QueryAppointmentById(val appointmentId: String) : RegistrationActions()
    data class SaveEditingAppointment(val editingAppointment: Appointment) : RegistrationActions()
    data class SaveLabTest(val labTestOrderForm: LabTestOrder) : RegistrationActions()
    data class SaveLabTestFile(val labTestOrderForm: LabTestOrder) : RegistrationActions()
    data class QueryMedicalExaminationById(val encounterId: String) : RegistrationActions()
    data class EligibleCustomer(val encounterId: String) : RegistrationActions()
    data class QueryServiceRating(val patientId: String, val encounterId: String) :
        RegistrationActions()

    data class SaveServiceRating(val serviceRatingForm: ServiceEvaluationCardByEncounter) :
        RegistrationActions()

    //    data class UploadImage(val file: MultipartBody.Part,val stringPatientId : RequestBody,val description : RequestBody
//    ,val type : RequestBody,val attackImageType : RequestBody ) : RegistrationActions()
    data class UploadImage(val body: RequestBody) : RegistrationActions()
    data class ShowNotifi(val body:Notification ) : RegistrationActions()
    data class searchFileDto(val body:Filedto) : RegistrationActions()
    data class searchFileDto2(val body:Filedto) : RegistrationActions()
    data class updateAttachFile(val body: AttachFileUpdate,val id: String) : RegistrationActions()
    data class deleteAttachFile(val id: String) : RegistrationActions()

    data class updateLabTest(val order : LabTestOrder) : RegistrationActions()

    object QueryPatientInfoData : RegistrationActions()
    object QueryCommonData4ProfileEdit : RegistrationActions()
    object RequirePersonalInfo : RegistrationActions()
    object RequirePrepRegistration : RegistrationActions()
    object SelectLabTests : RegistrationActions()
    object InputLabResults : RegistrationActions()
    object MakeAnAppointment : RegistrationActions()
    object ResetEditProfileData : RegistrationActions()
    object QueryCommonDataLabTestEdit : RegistrationActions()
    object QueryStepMessage : RegistrationActions()
    object QueryLabTestResult : RegistrationActions()
    object MedicalExamination : RegistrationActions()
    object ServiceRating : RegistrationActions()
    object CheckSupportStaffOnline:RegistrationActions()
    data class CheckDuplicateRegistration(val patient: Patient):RegistrationActions()
}
