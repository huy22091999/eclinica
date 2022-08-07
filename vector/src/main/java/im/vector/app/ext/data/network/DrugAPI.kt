package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.Drug
import im.vector.app.ext.data.model.DrugFilter
import im.vector.app.ext.data.model.Page
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Singleton

@Singleton
interface DrugAPI {

    // Drug dictionary

    @POST("api/drug/searchByDto")
    fun queryList(@Body filter: DrugFilter): Observable<Page<Drug>>

    @GET("api/drug/{id}")
    fun queryOne(@Path("id") id: String): Observable<Drug>

    // Drug prescriptions

}
