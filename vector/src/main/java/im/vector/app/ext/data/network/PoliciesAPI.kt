package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.PatientInfo
import im.vector.app.ext.data.model.Policies
import io.reactivex.Observable
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface PoliciesAPI {
    @GET("public/api/policies")
        fun queryPolicies(): Observable<List<Policies>>

}
