package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.network.HealthOrgAPI
import im.vector.app.ext.data.network.LabTestAPI
import im.vector.app.ext.data.network.PatientAPI
import im.vector.app.ext.enrollment.CheckDuplicateResponse
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.Path
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LabTestRepository @Inject constructor(
    private val api: LabTestAPI
) {
    fun getLatestOrder(): Observable<LabTestOrder> =
        api.queryLatestOrder().subscribeOn(Schedulers.io())
    fun saveLabTestOrder(order: LabTestOrder, id: String): Observable<LabTestOrder> = api.updateOneOrder(order, id).subscribeOn(Schedulers.io())
}
