package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.network.AppointmentAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepository @Inject constructor(
    private val api: AppointmentAPI
) {
    fun generateEncounterSlot(filter: EncounterSlotFilter): Observable<List<EncounterSlot>> =
        api.generateEncounterSlot(filter).subscribeOn(Schedulers.io())

    fun getById(id: String): Observable<Appointment> = api.queryOne(id).subscribeOn(Schedulers.io())

    fun saveAppointment(appointment: Appointment, id: String? = null): Observable<Appointment> =
        if (id == null ) {
            api.createOne(appointment).subscribeOn(Schedulers.io())
        }
        else {
            api.updateOne(appointment, id).subscribeOn(Schedulers.io())
        }
}
