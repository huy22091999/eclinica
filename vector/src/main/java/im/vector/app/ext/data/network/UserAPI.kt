package im.vector.app.ext.data.network

import im.vector.app.ext.data.model.Role
import io.reactivex.Observable
import retrofit2.http.GET
import javax.inject.Singleton

@Singleton
interface UserAPI {

    @GET("api/user/getAllRole")
    fun queryRoles(): Observable<List<Role>>

}
