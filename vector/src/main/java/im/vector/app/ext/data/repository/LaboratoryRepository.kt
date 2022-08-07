package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.AffiliatedLab
import im.vector.app.ext.data.model.Page
import im.vector.app.ext.data.model.QueryFilter
import im.vector.app.ext.data.network.AffiliatedLabAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LaboratoryRepository @Inject constructor(
    private val affiliatedLabApi: AffiliatedLabAPI
) {

    //////////// Affiliated lab //////////////////

    // query for list of affiliated labs
    fun getAffiliatedLabs(filter: QueryFilter): Observable<Page<AffiliatedLab>> =
        affiliatedLabApi.queryList(filter).subscribeOn(Schedulers.io())

    // query for one affiliated lab
    fun getOneAffiliatedLab(id: String): Observable<AffiliatedLab> =
        affiliatedLabApi.queryOne(id).subscribeOn(Schedulers.io())


}
