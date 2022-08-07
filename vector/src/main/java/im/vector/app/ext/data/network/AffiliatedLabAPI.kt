package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Singleton

@Singleton
interface AffiliatedLabAPI {

    // Labs

    @POST("api/affiliate-lab/searchByDto")
    fun queryList(@Body filter: QueryFilter): Observable<Page<AffiliatedLab>>

    @GET("api/affiliate-lab/{id}")
    fun queryOne(@Path("id") id: String): Observable<AffiliatedLab>

    // Lab test by affiliated labs

    @POST("api/affiliate-lab-lab-test/searchByDto")
    fun queryListLabTests(@Body filter: AffiliatedLabTestFilter): Observable<Page<AffiliatedLab_LabTest>>

    @GET("api/affiliate-lab-lab-test/{id}")
    fun queryOneLabTest(@Path("id") id: String): Observable<AffiliatedLab_LabTest>
    
}
