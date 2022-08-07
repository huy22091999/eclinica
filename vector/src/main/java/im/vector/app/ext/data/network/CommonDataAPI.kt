package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.*
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import javax.inject.Singleton

@Singleton
interface CommonDataAPI {

    // Administrative Units

    @GET("api/administrative-unit/getRootUnit")
    fun queryRootAdminUnits(): Observable<List<AdminUnit>>

    @GET("api/administrative-unit/getAllChildByParentId/{id}")
    fun queryAdminUnitsByParentId(@Path("id") parentId: String): Observable<List<AdminUnit>>

    @GET("api/administrative-unit/{id}")
    fun queryAdminUnit(@Path("id") id: String): Observable<AdminUnit>

    // Occupations
    @POST("api/occupation/searchByDto")
    fun queryOccupations(@Body filter: QueryFilter): Observable<Page<Occupation>>

    // Ethnicity
    @POST("api/ethnicity/searchByDto")
    fun queryEthnics(@Body filter: QueryFilter): Observable<Page<Ethnicity>>

}
