package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.AffiliatedLab
import im.vector.app.ext.data.model.Page
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.data.model.QueryFilter
import io.reactivex.Observable
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface PrescriptionAPI {

    @POST("api/prescription/searchByDto")
    fun searchByPage(@Body filter: QueryFilter): Observable<Page<Prescription>>

    @GET("api/prescription/{id}")
    fun queryOneById(@Path("id") id: String): Observable<Prescription>

    @PUT("api/prescription/updateReceive/{id}")
    fun updateReceive(@Body prescription: Prescription, @Path("id")id: String) : Observable<Prescription>

    @PUT("api/prescription/update-status/{id}")
    fun updateStatusReceived(@Body prescription: Prescription, @Path("id")id: String) : Observable<Prescription>
}
