package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.AffiliatedLab
import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.data.model.Page
import im.vector.app.ext.data.model.QueryFilter
import im.vector.app.ext.data.network.LabTestAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LabTestOrderRepository  @Inject constructor(
    private val labTestAPI: LabTestAPI
) {

    //get page labtest order
    fun getListLabTestOrder(filter: QueryFilter): Observable<Page<LabTestOrder>> =
        labTestAPI.queryListOrders(filter).subscribeOn(Schedulers.io())

    fun getOneById(id: String): Observable<LabTestOrder> =
        labTestAPI.queryOneOrder(id).subscribeOn(Schedulers.io())
}
