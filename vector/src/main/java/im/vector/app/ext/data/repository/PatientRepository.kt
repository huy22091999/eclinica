package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.network.PatientAPI
import im.vector.app.ext.enrollment.CheckDuplicateResponse
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PatientRepository @Inject constructor(
    private val api: PatientAPI
) {

    // Get the logged in patient
    fun getLoginPatient(): Observable<Patient> = api.queryLoginPatient().subscribeOn(Schedulers.io())

    // Get patient info
    fun getPatientInfo(): Observable<PatientInfo> = api.queryPatientInfo().subscribeOn(Schedulers.io())

    fun getPatientById(id: String): Observable<Patient> = api.queryPatientById(id).subscribeOn(Schedulers.io())

    // Update patient
    fun updatePatient(patient: Patient, id: String): Observable<Patient> =
        api.updatePatient(patient, id).subscribeOn(Schedulers.io())

    // Prep screening
    fun getPatientPeriodPrepScreening(id: String): Observable<PatientPeriodPrepScreen> =
        api.queryPatientPeriodPrepScreening(id).subscribeOn(Schedulers.io())

    fun savePatientPeriodPrepScreening(
        form: PatientPeriodPrepScreen,
        id: String? = null
    ): Observable<PatientPeriodPrepScreen> = if (id == null) {
        api.createPatientPeriodPrepScreening(form).subscribeOn(Schedulers.io())
    } else {
        api.updatePatientPeriodPrepScreening(form, id).subscribeOn(Schedulers.io())
    }

    // Register client
    fun registerClient(client: ClientRegistration): Observable<Unit> =
        api.registerClient(client).subscribeOn(Schedulers.io())

    // Check duplicate when registering client
    fun checkDuplicate(username: String?, phoneNumber: String?): Observable<CheckDuplicateResponse> =
        api.checkRegistrationInfoDuplicate(username, phoneNumber).subscribeOn(Schedulers.io())

    //Get step message
    fun getStepMessage(): Observable<ClientStepMessage> = api.queryStepMessage().subscribeOn(Schedulers.io())


    fun uploadImage(body: RequestBody): Observable<PatientMedicalRecordFile>
            = api.uploadImage(body).subscribeOn(Schedulers.io())


    fun getNotification(notifi: Notification): Observable<ItemNotification> =
        api.searchNotification(notifi).subscribeOn(Schedulers.io())


    fun searchFileDto(filedto:Filedto) : Observable<FiledtoItems> = api.searchImageFile(filedto).subscribeOn(Schedulers.io())

    fun updateAttachFile(body:AttachFileUpdate, id: String) :  Observable<PatientMedicalRecordFile> = api.updateAttachFile(body,id).subscribeOn(Schedulers.io())
    fun deleteAttachFile(id: String) :  Observable<Boolean> = api.deleteAttachFile(id).subscribeOn(Schedulers.io())

    fun checkSupportOnline():Observable<Boolean> = api.checkSupportOnline().subscribeOn(Schedulers.io())
    fun checkDuplicateRegistration(patient: Patient):Observable<CheckDuplicate> = api.checkDuplicateEmail(patient).subscribeOn(Schedulers.io())

    fun checkPasswordUserExist(info:InformationResetPassword):Observable<Boolean>
        = api.checkPasswordUserExist(info).subscribeOn(Schedulers.io())
    fun sendEmailResetPassword(info:InformationResetPassword):Observable<Unit>
            = api.sendEmailResetPassword(info).subscribeOn(Schedulers.io())

}
