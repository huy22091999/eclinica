package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.Policies
import im.vector.app.ext.data.network.PoliciesAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PoliciesRepository @Inject constructor(private val api: PoliciesAPI){

    fun getPolicies(): Observable<List<Policies>> = api.queryPolicies().subscribeOn(Schedulers.io())

}
