package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.Role
import im.vector.app.ext.data.network.UserAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: UserAPI
) {

    // query all roles
    fun getAllRoles(): Observable<List<Role>> = api.queryRoles().subscribeOn(Schedulers.io())

}
