package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.Drug
import im.vector.app.ext.data.model.DrugFilter
import im.vector.app.ext.data.model.Page
import im.vector.app.ext.data.network.DrugAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrugRepository @Inject constructor(
    private val api: DrugAPI
) {

    // query a list of drugs
    fun getDrugs(filter: DrugFilter): Observable<Page<Drug>> = api.queryList(filter).subscribeOn(Schedulers.io())

    // query a drug
    fun getDrug(id: String): Observable<Drug> = api.queryOne(id).subscribeOn(Schedulers.io())


}
