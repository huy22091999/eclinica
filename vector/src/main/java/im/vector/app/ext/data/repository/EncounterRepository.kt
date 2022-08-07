package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.Appointment
import im.vector.app.ext.data.model.MedicalExamination
import im.vector.app.ext.data.model.ServiceEvaluationCardByEncounter
import im.vector.app.ext.data.model.TreatmentPeriod
import im.vector.app.ext.data.network.AppointmentAPI
import im.vector.app.ext.data.network.EncounterAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncounterRepository @Inject constructor(
    private val api: EncounterAPI
)  {
    fun getMedicalExaminationId(id: String): Observable<MedicalExamination> =
        api.queryMedicalExamination(id).subscribeOn(Schedulers.io())

    fun patientAgree(id: String): Observable<MedicalExamination> =
        api.patientAgree(id).subscribeOn(Schedulers.io())

    fun getServiceRating(patientId: String, encounterId: String) : Observable<ServiceEvaluationCardByEncounter> =
        api.queryEncounterServiceRating(patientId, encounterId).subscribeOn(Schedulers.io())

    fun saveServiceRating(form: ServiceEvaluationCardByEncounter, id: String? = null)
    : Observable<ServiceEvaluationCardByEncounter> =
        if (id == null) {
            api.createServiceRating(form).subscribeOn(Schedulers.io())
        } else {
            api.updateServiceRating(form, id).subscribeOn(Schedulers.io())
        }

    fun getTreatmentPeriodByPatientId(patientId: String): Observable<List<TreatmentPeriod>> =
        api.queryTreatmentPeriodByPatientId(patientId).subscribeOn(Schedulers.io())


}
