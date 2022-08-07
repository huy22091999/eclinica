package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.*
import io.reactivex.Observable
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface EncounterAPI {

    @GET("api/encounter/medicalExamination/{id}")
    fun queryMedicalExamination(@Path("id") id: String): Observable<MedicalExamination>

    @GET("api/encounter/patientAgree/{id}")
    fun patientAgree(@Path("id") id: String): Observable<MedicalExamination>

    @GET("api/serviceEvaluationCardByEncounter/{patientId}/{encounterId}")
    fun queryEncounterServiceRating(@Path("patientId") patientId: String,
                                    @Path("encounterId") encounterId: String) :
            Observable<ServiceEvaluationCardByEncounter>

    @POST("api/serviceEvaluationCardByEncounter")
    fun createServiceRating(@Body form: ServiceEvaluationCardByEncounter) :
            Observable<ServiceEvaluationCardByEncounter>

    @PUT("api/serviceEvaluationCardByEncounter/{id}")
    fun updateServiceRating(@Body form: ServiceEvaluationCardByEncounter, @Path("id") id: String) :
            Observable<ServiceEvaluationCardByEncounter>

    @GET("api/treatmentperiod/get-by")
    fun queryTreatmentPeriodByPatientId(@Query("patient-id") patientId: String? = null) :
            Observable<List<TreatmentPeriod>>
}
