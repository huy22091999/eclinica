package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.Page
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.data.model.QueryFilter
import im.vector.app.ext.data.network.PrescriptionAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrescriptionRepository @Inject constructor(
    private val api: PrescriptionAPI
) {

    fun searchByPage(filter: QueryFilter): Observable<Page<Prescription>> =
        api.searchByPage(filter).subscribeOn(Schedulers.io())

    fun getOneById(id: String): Observable<Prescription> =
        api.queryOneById(id).subscribeOn(Schedulers.io())

    fun updateReceive(prescription: Prescription, id: String): Observable<Prescription> =
        api.updateReceive(prescription, id).subscribeOn(Schedulers.io())

    fun updateStatusReceived(prescription: Prescription, id: String): Observable<Prescription> =
        api.updateStatusReceived(prescription, id).subscribeOn(Schedulers.io())
}
