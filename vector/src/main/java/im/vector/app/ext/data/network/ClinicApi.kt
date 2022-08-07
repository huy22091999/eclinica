package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.Appointment
import im.vector.app.ext.data.model.Clinic
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface ClinicApi {
    @GET("https://telehealthmanager-api.teleprep.vn/telehealthManager/public/clinics")
    fun getClinics(): Observable<List<Clinic>>
}
