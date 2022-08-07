package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.network.HealthOrgAPI
import im.vector.app.ext.data.network.PatientAPI
import im.vector.app.ext.enrollment.CheckDuplicateResponse
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthOrgRepository @Inject constructor(
    private val api: HealthOrgAPI
) {
    fun getHealthOrg(form: HealthOrgFilter): Observable<Page<HealthOrganization>> = api.searchByDto(form).subscribeOn(Schedulers.io())
}
