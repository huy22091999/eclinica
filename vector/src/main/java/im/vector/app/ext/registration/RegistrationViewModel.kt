package im.vector.app.ext.registration

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.extensions.exhaustive
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.repository.*
import im.vector.app.ext.data.type.AddressType
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*

class RegistrationViewModel @AssistedInject constructor(
    @Assisted initialState: RegistrationViewState,
    private val patientRepository: PatientRepository,
    private val healthOrgRepository: HealthOrgRepository,
    private val commonDataRepository: CommonDataRepository,
    private val labTestRepository: LabTestRepository,
    private val fileUploadRepository: FileUploadRepository,
    private val appointmentRepository: AppointmentRepository,
    private val encounterRepository: EncounterRepository,
) : VectorViewModel<RegistrationViewState, RegistrationActions, RegistrationViewEvents>(initialState) {

    private val defaultFilter = QueryFilter(pageIndex = 1, pageSize = 100)

    @AssistedFactory
    interface Factory {
        fun create(initialState: RegistrationViewState): RegistrationViewModel
    }

    companion object : MvRxViewModelFactory<RegistrationViewModel, RegistrationViewState> {

        override fun initialState(viewModelContext: ViewModelContext): RegistrationViewState {
            return RegistrationViewState(
                currentEditScreen = null,
                asyncPatientInfo = Uninitialized,
                asyncPatient = Uninitialized,
                asyncEthnics = Uninitialized,
                asyncOccupations = Uninitialized,
                asyncProvinces = Uninitialized,
                asyncDistricts = Uninitialized,
                asyncDistricts2 = Uninitialized,
                asyncCommunes = Uninitialized,
                asyncCommunes2 = Uninitialized,
                editingPatient = null,
//                attachFile = null,
                asyncHealthOrganization = Uninitialized,
                asyncLabTestOrder = Uninitialized,
                asyncStepMessage = Uninitialized,
                asyncEncounterSlot = Uninitialized,
                asyncEditingPatient = Uninitialized,
                asyncMedicalExamination = Uninitialized,
                asyncEditingSelectLabTestOrder = Uninitialized,
                asyncEditingSelectLabTestOrderFile = Uninitialized,
                asyncFileAttachment = Uninitialized,
                asyncServiceRating = Uninitialized,
                asyncAttachedFile = Uninitialized,
                asyncFileDto = Uninitialized,
                asyncFileDto2 = Uninitialized,
                supportOnline = Uninitialized
            )
        }

        @JvmStatic
        override fun create(
            viewModelContext: ViewModelContext,
            state: RegistrationViewState
        ): RegistrationViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state)
                ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

//    init {
//        handleQueryPatientInfoData()
//        handleQueryStepMessage()
//    }

    override fun handle(action: RegistrationActions) {
        when (action) {
            is RegistrationActions.QueryPatientData -> handleQueryPatientData(action.patientId)

            is RegistrationActions.QueryPatientInfoData -> handleQueryPatientInfoData()

            is RegistrationActions.QueryPeriodPrepScreen -> handleQueryPeriodPrepScreen(action.screeningId)

            is RegistrationActions.RequirePersonalInfo -> {
                _viewEvents.post(RegistrationViewEvents.LaunchProfileEditFragment)
            }

            is RegistrationActions.RequirePrepRegistration -> {
                _viewEvents.post(RegistrationViewEvents.LaunchPrepRegistrationFragment)
            }

            is RegistrationActions.SelectLabTests -> {
                _viewEvents.post(RegistrationViewEvents.LaunchLabTestSelectionFragment)
            }

            is RegistrationActions.InputLabResults -> {
                _viewEvents.post(RegistrationViewEvents.LaunchLabTestResultFragment)
            }

            is RegistrationActions.MakeAnAppointment -> {
                _viewEvents.post(RegistrationViewEvents.LaunchAppointmentSetupFragment)
            }

            is RegistrationActions.QueryCommonData4ProfileEdit -> handleQueryCommonData4ProfileEdit()
            is RegistrationActions.QueryCommonDataLabTestEdit -> handleQueryCommonDataLabTestEdit()

            is RegistrationActions.ProvinceSelected -> handleSelectProvince(
                action.id,
                action.addressType,
                action.clearCommune
            )
            is RegistrationActions.ProvinceHealthOrgSelected -> handleSelectProvinceHealthOrg(action.code)
            is RegistrationActions.DistrictSelected -> handleSelectDistrict(
                action.id,
                action.addressType
            )

            is RegistrationActions.UpdatePatientData -> {
                setState {
                    copy(editingPatient = action.editingPatient)
                }
            }

            is RegistrationActions.SaveEditingPatient -> handleSaveEditingPatient(action.editingPatient)

            is RegistrationActions.SaveScreeningResult -> handleSaveScreeningResult(action.screeningForm)

            is RegistrationActions.SaveLabTest -> handleSaveChoosenLabTest(action.labTestOrderForm)

            is RegistrationActions.ResetEditProfileData -> {
                setState {
                    copy(
                        asyncEthnics = Uninitialized,
                        asyncOccupations = Uninitialized,
                        asyncProvinces = Uninitialized,
                        asyncDistricts = Uninitialized,
                        asyncDistricts2 = Uninitialized,
                        asyncCommunes = Uninitialized,
                        asyncCommunes2 = Uninitialized,
                        asyncEditingPatient = Uninitialized,
                        editingPatient = null,
                        asyncAttachedFile = Uninitialized
                    )
                }
            }

            is RegistrationActions.QueryStepMessage -> handleQueryStepMessage()

            is RegistrationActions.QueryLabTestResult -> handleQueryLabTestResult()

            is RegistrationActions.UploadLabTestResultFile -> handleUploadLabTestResultFile(action.fileBody)

            is RegistrationActions.GenerateEncounterSlot -> handleGenerateEncounterSlot(action.currentDate)

            is RegistrationActions.QueryAppointmentById -> handleQueryAppointmentById(action.appointmentId)

            is RegistrationActions.SaveEditingAppointment -> handleSaveEditingAppointment(action.editingAppointment)

            is RegistrationActions.MedicalExamination -> {
                _viewEvents.post(RegistrationViewEvents.LaunchMedicalExaminationFragment)
            }

            is RegistrationActions.QueryMedicalExaminationById -> handleQueryEncounterById(action.encounterId)

            is RegistrationActions.EligibleCustomer -> handleEligibleCustomer(action.encounterId)

            is RegistrationActions.ServiceRating -> {
                _viewEvents.post(RegistrationViewEvents.LaunchServiceRatingFragment)
            }

            is RegistrationActions.QueryServiceRating -> handleQueryServiceRating(
                action.patientId,
                action.encounterId
            )

            is RegistrationActions.SaveServiceRating -> handleSaveServiceRating(action.serviceRatingForm)

//            is RegistrationActions.UploadImage ->  handleloadImage(action.file,action.stringPatientId,action.description,action.type,
//            action.attackImageType)
            is RegistrationActions.UploadImage -> handleloadImage(action.body)

            is RegistrationActions.ShowNotifi -> handleShowNotification(action.body)

            is RegistrationActions.searchFileDto -> handlesearchFileDto(action.body)
            is RegistrationActions.searchFileDto2 -> handlesearchFileDto2(action.body)

            is RegistrationActions.updateAttachFile -> handleUpdateAttachFile(
                action.body,
                action.id
            )
            is RegistrationActions.deleteAttachFile -> handleDeleteAttachFile(action.id)
            is RegistrationActions.updateLabTest -> handleupdateLabTest(action.order)
            is RegistrationActions.CheckSupportStaffOnline -> handeCheckSupportOnline()
            is RegistrationActions.CheckDuplicateRegistration -> handeCheckDuplicate(action.patient)

        }
    }

    private fun handeCheckDuplicate(patient: Patient) {
        setState {
            copy(asyncCheckDuplicate = Loading())
        }
        patientRepository.checkDuplicateRegistration(patient).execute {
            _viewEvents.post(RegistrationViewEvents.CheckDuplicate)
            copy(asyncCheckDuplicate = it)
        }

    }

    private fun handeCheckSupportOnline() {
        setState {
            copy(supportOnline = Loading())
        }
        patientRepository.checkSupportOnline().execute {
            supportOnline.invoke().let {
                _viewEvents.post(RegistrationViewEvents.CheckSupportOnlineMessage)
            }
            copy(supportOnline = it)
        }

    }

    private fun handleUploadLabTestResultFile(fileBody: MultipartBody.Part) =
        viewModelScope.launch {
            setState {
                copy(asyncFileAttachment = Loading())
            }
            fileUploadRepository.uploadLabTestResultFile(fileBody).execute {
                copy(asyncFileAttachment = it)
            }

        }


    private fun handleQueryLabTestResult() {
        setState {
            copy(
                currentEditScreen = RegistrationSection3Fragment.TAG,
                asyncLabTestOrder = Loading(),
            )
        }
        labTestRepository.getLatestOrder().execute {
            copy(asyncLabTestOrder = it)
        }
    }

    private fun handleQueryStepMessage() {
        setState {
            copy(asyncStepMessage = Loading())
        }
        patientRepository.getStepMessage().execute {
            copy(asyncStepMessage = it)
        }
    }

    private fun handleQueryPatientData(patientId: String) {
        setState {
            copy(asyncPatient = Loading())
        }
        patientRepository.getPatientById(patientId).execute {
            copy(
                asyncPatient = it,
                editingPatient = it.invoke()
            )
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

    private fun handleQueryPeriodPrepScreen(screeningId: String?) {
        setState {
            copy(asyncPrepScreen = Loading())
        }

        if (screeningId != null) {
            patientRepository.getPatientPeriodPrepScreening(screeningId).execute {
                copy(asyncPrepScreen = it)
            }
        } else {
            setState {
                copy(asyncPrepScreen = Success(PatientPeriodPrepScreen(id = null)))
            }
        }
    }

    private fun handleSelectProvinceHealthOrg(code: String?) {
        setState {
            copy(asyncHealthOrganization = Loading())
        }
        val searchDto = HealthOrgFilter(adminUnitCode = code, types = mutableListOf(2, 5))
        healthOrgRepository.getHealthOrg(searchDto).execute {
            asyncHealthOrganization.invoke().let {
                _viewEvents.post(RegistrationViewEvents.ApplyNewHealthOrg)
            }
            copy(asyncHealthOrganization = it)
        }
    }

    private fun handleSelectProvince(
        id: String,
        addressType: AddressType,
        clearCommune: Boolean? = true
    ) {
        setState {
            if (addressType == AddressType.PERM) {
                copy(asyncDistricts = Loading(), asyncCommunes = Uninitialized)
            } else {
                copy(asyncDistricts2 = Loading(), asyncCommunes2 = Uninitialized)
            }
        }

        commonDataRepository.getAdminUnits(id).execute {
            if (addressType == AddressType.PERM) {
                asyncDistricts.invoke().let {
                    _viewEvents.post(
                        RegistrationViewEvents.ApplyNewDistricts(
                            addressType,
                            clearCommune
                        )
                    )
                }

                copy(asyncDistricts = it)
            } else {
                asyncDistricts2.invoke().let {
                    _viewEvents.post(
                        RegistrationViewEvents.ApplyNewDistricts(
                            addressType,
                            clearCommune
                        )
                    )
                }

                copy(asyncDistricts2 = it)
            }
        }
    }

    private fun handleSelectDistrict(id: String, addressType: AddressType) {
        setState {
            if (addressType == AddressType.PERM) {
                copy(asyncCommunes = Loading())
            } else {
                copy(asyncCommunes2 = Loading())
            }
        }

        commonDataRepository.getAdminUnits(id).execute {
            if (addressType == AddressType.PERM) {
                asyncCommunes.invoke().let {
                    _viewEvents.post(RegistrationViewEvents.ApplyNewCommunes(addressType))
                }

                copy(asyncCommunes = it)
            } else {
                asyncCommunes2.invoke().let {
                    _viewEvents.post(RegistrationViewEvents.ApplyNewCommunes(addressType))
                }

                copy(asyncCommunes2 = it)
            }
        }
    }

    private fun handleSaveEditingPatient(editingPatient: Patient) {
        setState {
            copy(asyncEditingPatient = Loading())
        }

        patientRepository.updatePatient(editingPatient, editingPatient.id!!).execute {
            asyncPatient.invoke()?.let {
                _viewEvents.post(RegistrationViewEvents.SavePatientComplete(it))
            }
            copy(asyncEditingPatient = it)
        }
    }

    private fun handleSaveServiceRating(serviceRatingForm: ServiceEvaluationCardByEncounter) {
        setState {
            copy(asyncEditingServiceRating = Loading())
        }

        encounterRepository.saveServiceRating(serviceRatingForm, serviceRatingForm.id).execute {
            _viewEvents.post(RegistrationViewEvents.SaveServiceRatingComplete)
            copy(asyncEditingServiceRating = it)
        }
    }

    private fun handleSaveScreeningResult(screeningForm: PatientPeriodPrepScreen) {
        setState {
            copy(asyncEditingPrepScreen = Loading())
        }

        patientRepository.savePatientPeriodPrepScreening(screeningForm, screeningForm.id).execute {
            asyncPrepScreen.invoke()?.let {
                _viewEvents.post(RegistrationViewEvents.SaveScreeningFormComplete)
            }
            copy(asyncEditingPrepScreen = it)
        }
    }

    private fun handleSaveChoosenLabTest(labTestOrderForm: LabTestOrder) = viewModelScope.launch {
        setState {
            copy(asyncEditingSelectLabTestOrder = Loading())
        }
        if (labTestOrderForm.id != null) {
            labTestRepository.saveLabTestOrder(labTestOrderForm, labTestOrderForm.id).execute {
                /*asyncLabTestOrder.invoke()?.let {
                    _viewEvents.post(RegistrationViewEvents.SaveSelectTestServiceComplete(it))
                }*/
                copy(asyncEditingSelectLabTestOrder = it)
            }
        }

    }


    private fun handleupdateLabTest(labTestOrderForm: LabTestOrder) {
        setState {
            copy(asyncEditingSelectLabTestOrder = Loading())
        }
        if (labTestOrderForm.id != null) {
            fileUploadRepository.updateLabTest(labTestOrderForm.id, labTestOrderForm)
                .execute { it ->
//                asyncLabTestOrder.invoke()?.let {
//                    _viewEvents.post(RegistrationViewEvents.SaveSelectTestServiceComplete(it))
//                }
                    copy(asyncEditingSelectLabTestOrder = it)
                }
        }

    }

    private fun handleQueryCommonData4ProfileEdit() {
        setState {
            copy(
                currentEditScreen = RegistrationSection0Fragment.TAG,
                asyncEthnics = Loading(),
                asyncOccupations = Loading(),
                asyncProvinces = Loading()
            )
        }

        commonDataRepository.getAdminUnits().execute {
            copy(asyncProvinces = it)
        }

        commonDataRepository.getOccupations(defaultFilter).execute {
            copy(asyncOccupations = it)
        }

        commonDataRepository.getEthnics(defaultFilter).execute {
            copy(asyncEthnics = it)
        }
    }

    private fun handleQueryCommonDataLabTestEdit() = viewModelScope.launch {
        setState {
            copy(
                currentEditScreen = RegistrationSection2Fragment.TAG,
                asyncLabTestOrder = Loading(),
                asyncProvinces = Loading()
            )
        }
        commonDataRepository.getAdminUnits().execute {
            copy(asyncProvinces = it)
        }
        labTestRepository.getLatestOrder().execute {
            copy(asyncLabTestOrder = it)
        }

    }

    private fun handleGenerateEncounterSlot(currentDate: Date) {
        setState {
            copy(asyncEncounterSlot = Loading())
        }
        appointmentRepository.generateEncounterSlot(EncounterSlotFilter(currentDate)).execute {
            _viewEvents.post(RegistrationViewEvents.GetEncounterSlot)
            copy(asyncEncounterSlot = it)
        }
    }

    private fun handleQueryAppointmentById(appointmentId: String) {
        setState {
            copy(asyncAppointment = Loading())
        }
        appointmentRepository.getById(appointmentId).execute {
            _viewEvents.post(RegistrationViewEvents.QueryAppointment)
            copy(asyncAppointment = it)

        }
    }

    private fun handleSaveEditingAppointment(editingAppointment: Appointment) {
        setState {
            copy(asyncEditingAppointment = Loading())
        }
        var id: String? = null
        if (editingAppointment.id != null) {
            id = editingAppointment.id
        }
        appointmentRepository.saveAppointment(editingAppointment, id).execute {
            asyncEditingAppointment.invoke()?.let {
                _viewEvents.post(RegistrationViewEvents.SaveAppointmentComplete(it))
            }
            copy(asyncEditingAppointment = it)
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
                _viewEvents.post(RegistrationViewEvents.EligibleCustomerComplete(it1))
            }
            copy(asyncMedicalExamination = it)
        }
    }

    private fun handleloadImage(body: RequestBody) {
        setState {
            copy(asyncAttachedFile = Loading())
        }
        patientRepository.uploadImage(body).execute {

            copy(asyncAttachedFile = it)

        }

    }

    private fun handleShowNotification(body: Notification) {
        patientRepository.getNotification(body).execute {
            copy(asyncNotification = it)
        }
    }

    private fun handlesearchFileDto(body: Filedto) {
        patientRepository.searchFileDto(body).execute {
            copy(asyncFileDto = it)
        }
    }

    private fun handlesearchFileDto2(body: Filedto) {
        patientRepository.searchFileDto(body).execute {
            copy(asyncFileDto2 = it)
        }
    }


    private fun handleUpdateAttachFile(body: AttachFileUpdate, id: String) {
        patientRepository.updateAttachFile(body, id).execute {
            copy(asyncAttachedFile = it)
        }
    }

    private fun handleDeleteAttachFile(id: String) {
        patientRepository.deleteAttachFile(id).execute {
            copy(asyncFileDeleted = it)
        }
    }

}
