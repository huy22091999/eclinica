package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.*
import im.vector.app.ext.enrollment.CheckDuplicateResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface PatientAPI {

    @GET("api/patient/getpatientinfo")
    fun queryPatientInfo(): Observable<PatientInfo>

    @GET("api/patient/getLoginPatient")
    fun queryLoginPatient(): Observable<Patient>

    @GET("api/patient/getOne/{id}")
    fun queryPatientById(@Path("id") id: String): Observable<Patient>

    /**
     * For client to update their information
     */
    @PUT("api/patient/update/current/{id}")
    fun updatePatient(@Body patient: Patient, @Path("id") id: String): Observable<Patient>

    /**
     * Get patientPeriodPrepScreening by ID
     */
    @GET("api/patient-period-prep-screening/{id}")
    fun queryPatientPeriodPrepScreening(@Path("id") id: String): Observable<PatientPeriodPrepScreen>

    @POST("api/patient-period-prep-screening")
    fun createPatientPeriodPrepScreening(@Body form: PatientPeriodPrepScreen): Observable<PatientPeriodPrepScreen>

    @PUT("api/patient-period-prep-screening/{id}")
    fun updatePatientPeriodPrepScreening(
        @Body form: PatientPeriodPrepScreen,
        @Path("id") id: String
    ): Observable<PatientPeriodPrepScreen>

    /**
     * Register a client
     */
    @POST("public/auth/register")
    fun registerClient(@Body registration: ClientRegistration): Observable<Unit>

    /**
     * Check duplicate username, phone number
     */
    @GET("public/auth/duplicated")
    fun checkRegistrationInfoDuplicate(
        @Query("username") username: String? = null,
        @Query("phone-number") phoneNumber: String? = null
    ): Observable<CheckDuplicateResponse>

    @GET("api/patient/getStepMessage")
    fun queryStepMessage(): Observable<ClientStepMessage>


    @POST("api/file/upload/patient/medical-record")
    fun uploadImage(
        @Body body: RequestBody
    ) : Observable<PatientMedicalRecordFile>


    @POST("api/notification/searchNotification")
    fun searchNotification(@Body notifi:Notification): Observable<ItemNotification>

    @POST("api/notification/searchNotification")
    fun searchNotificationuseCall(@Body notifi:Notification): Call<ItemNotification>

    // load file to app
    @POST("api/patient-medical-record-file/searchByDto")
    fun  searchImageFile(@Body filedto:Filedto): Observable<FiledtoItems>

    // put,delete info attach file
    @PUT("api/patient-medical-record-file/{id}")
    fun updateAttachFile(@Body body:AttachFileUpdate ,@Path("id") id: String): Observable<PatientMedicalRecordFile>

    @DELETE("api/patient-medical-record-file/{id}")
    fun deleteAttachFile(@Path("id") id: String): Observable<Boolean>

    @GET("api/patient-support-staff/checkSupportStaffOnline")
    fun checkSupportOnline():Observable<Boolean>

    @POST("public/api/checkForgotUserExist")
    fun checkPasswordUserExist(@Body body:InformationResetPassword):Observable<Boolean>
    @POST("public/api/checkForgotUserExist")
    fun sendEmailResetPassword(@Body body:InformationResetPassword):Observable<Unit>
    @POST("api/patient/duplicate-patient-check")
    fun checkDuplicateEmail(@Body patient: Patient):Observable<CheckDuplicate>

}
