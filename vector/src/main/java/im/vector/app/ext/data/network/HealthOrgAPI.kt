package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.*
import im.vector.app.ext.enrollment.CheckDuplicateResponse
import io.reactivex.Observable
import retrofit2.http.*
import javax.inject.Singleton

@Singleton
interface HealthOrgAPI {

    @POST("api/health-organization/searchByDto")
    fun searchByDto(@Body form: HealthOrgFilter): Observable<Page<HealthOrganization>>

}
