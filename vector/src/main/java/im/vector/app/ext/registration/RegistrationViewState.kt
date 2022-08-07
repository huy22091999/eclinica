package im.vector.app.ext.registration

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.ext.data.model.*

data class RegistrationViewState(

    var currentEditScreen: String? = null,

    var asyncPatientInfo: Async<PatientInfo> = Uninitialized,

    // ------------------------------------------------
    // for profile edit
    // ------------------------------------------------
    var asyncPatient: Async<Patient> = Uninitialized,

    var asyncEthnics: Async<Page<Ethnicity>> = Uninitialized,
    var asyncOccupations: Async<Page<Occupation>> = Uninitialized,
    var asyncProvinces: Async<List<AdminUnit>> = Uninitialized,
    var asyncHealthOrganization: Async<Page<HealthOrganization>>,
    // --> dia chi thuong tru
    var asyncDistricts: Async<List<AdminUnit>> = Uninitialized,
    var asyncCommunes: Async<List<AdminUnit>> = Uninitialized,
    // --> dia chi tam tru
    var asyncDistricts2: Async<List<AdminUnit>> = Uninitialized,
    var asyncCommunes2: Async<List<AdminUnit>> = Uninitialized,

    var editingPatient: Patient? = null,
    var asyncEditingPatient: Async<Patient>? = Uninitialized,
    var asyncAttachedFile: Async<PatientMedicalRecordFile>? = Uninitialized,
    var asyncFileDto: Async<FiledtoItems> = Uninitialized,
    var asyncFileDto2: Async<FiledtoItems> = Uninitialized,
    var asyncFileDeleted: Async<Boolean> = Uninitialized,
//    var attachFile:PatientMedicalRecordFile?= null,
    // ------------------------------------------------
    // for PrEP screening
    // ------------------------------------------------
    var asyncPrepScreen: Async<PatientPeriodPrepScreen> = Uninitialized,
    var asyncEditingPrepScreen: Async<PatientPeriodPrepScreen> = Uninitialized,
    var asyncLabTestOrder: Async<LabTestOrder> = Uninitialized,
    var asyncEditingSelectLabTestOrder: Async<LabTestOrder> = Uninitialized,
    var asyncEditingSelectLabTestOrderFile: Async<LabTestOrder> = Uninitialized,
    var asyncStepMessage: Async<ClientStepMessage> = Uninitialized,

    var asyncEncounterSlot: Async<List<EncounterSlot>> = Uninitialized,
    var asyncAppointment: Async<Appointment> = Uninitialized,
    var asyncEditingAppointment: Async<Appointment> = Uninitialized,

    var asyncMedicalExamination: Async<MedicalExamination> = Uninitialized,
    var asyncFileAttachment: Async<LabTestOrderAttachment> = Uninitialized,
    var asyncFileAttachment2: Async<FiledtoContent> = Uninitialized,

    var asyncServiceRating: Async<ServiceEvaluationCardByEncounter> = Uninitialized,
    var asyncEditingServiceRating: Async<ServiceEvaluationCardByEncounter> = Uninitialized,

    var asyncNotification: Async<ItemNotification> = Uninitialized,

    var supportOnline: Async<Boolean> = Uninitialized,
    var asyncCheckDuplicate:Async<CheckDuplicate> = Uninitialized

) : MvRxState {

    fun isLoading() =
        asyncPatient is Loading
                || asyncEditingPatient is Loading
                || asyncPatientInfo is Loading
                || asyncEthnics is Loading
                || asyncOccupations is Loading
                || asyncProvinces is Loading
                || asyncDistricts is Loading
                || asyncDistricts2 is Loading
                || asyncCommunes is Loading
                || asyncCommunes2 is Loading
                || asyncPrepScreen is Loading
                || asyncEditingPrepScreen is Loading
                || asyncHealthOrganization is Loading
                || asyncLabTestOrder is Loading
                || asyncStepMessage is Loading
                || asyncEncounterSlot is Loading
                || asyncAppointment is Loading
                || asyncEditingAppointment is Loading
                || asyncMedicalExamination is Loading
                || asyncEditingSelectLabTestOrder is Loading
                //|| asyncEditingSelectLabTestOrderFile is Loading
                || asyncFileAttachment is Loading
                || asyncServiceRating is Loading
                || asyncEditingServiceRating is Loading
                || supportOnline is Loading
                || asyncCheckDuplicate is Loading

    fun notLoading() =
        asyncPatient !is Loading
                && asyncEditingPatient !is Loading
                && asyncPatientInfo !is Loading
                && asyncEthnics !is Loading
                && asyncOccupations !is Loading
                && asyncProvinces !is Loading
                && asyncDistricts !is Loading
                && asyncDistricts2 !is Loading
                && asyncCommunes !is Loading
                && asyncCommunes2 !is Loading
                && asyncPrepScreen !is Loading
                && asyncEditingPrepScreen !is Loading
                && asyncHealthOrganization !is Loading
                && asyncLabTestOrder !is Loading
                && asyncStepMessage !is Loading
                && asyncEncounterSlot !is Loading
                && asyncAppointment !is Loading
                && asyncEditingAppointment !is Loading
                && asyncMedicalExamination !is Loading
                && asyncEditingSelectLabTestOrder !is Loading
                //&& asyncEditingSelectLabTestOrderFile !is Loading
                && asyncFileAttachment !is Loading
                && asyncServiceRating !is Loading
                && asyncEditingServiceRating !is Loading
                && supportOnline !is Loading
                && asyncCheckDuplicate !is Loading
}
