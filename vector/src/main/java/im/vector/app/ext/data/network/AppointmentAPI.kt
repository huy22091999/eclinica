package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.*
import io.reactivex.Observable
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface AppointmentAPI {

    @POST("api/appointment/searchByDto")
    fun queryList(@Body filter: AppointmentFilter): Observable<Page<Appointment>>

    @GET("api/appointment/{id}")
    fun queryOne(@Path("id") id: String): Observable<Appointment>

    @POST("api/appointment/create")
    fun createOne(@Body appointment: Appointment): Observable<Appointment>

    @PUT("api/appointment/update/{id}")
    fun updateOne(@Body appointment: Appointment, @Path("id") id: String): Observable<Appointment>

    @DELETE("api/appointment/{id}")
    fun deleteOne(@Path("id") id: String): Observable<Appointment>

    // Conference
    @GET("api/appointment/getConversationId/{id}")
    fun getConferenceId(@Path("id") appointmentId: String): Observable<String>

    @GET("api/appointment/acceptConversationId/{id}")
    fun acceptConference(@Path("id") appointmentId: String): Observable<Appointment>

    @POST("api/encounter-slot/generateEncounterSlot")
    fun generateEncounterSlot(@Body encounterSlotFilter: EncounterSlotFilter): Observable<List<EncounterSlot>>

}
