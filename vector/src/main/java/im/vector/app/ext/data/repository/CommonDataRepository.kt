package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.network.CommonDataAPI
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonDataRepository @Inject constructor(
    private val api: CommonDataAPI
) {

    /**
     * Get a list of admin units
     */
    fun getAdminUnits(parentId: String? = null): Observable<List<AdminUnit>> {
        val observable = if (parentId == null) {
            api.queryRootAdminUnits()
        } else {
            api.queryAdminUnitsByParentId(parentId)
        }

        return observable.subscribeOn(Schedulers.io())
    }

    /**
     * Get a specific admin unit
     */
    fun getAdminUnit(id: String): Observable<AdminUnit> = api.queryAdminUnit(id).subscribeOn(Schedulers.io())

    /**
     * Get a list of occupations
     */
    fun getOccupations(filter: QueryFilter): Observable<Page<Occupation>> =
        api.queryOccupations(filter).subscribeOn(Schedulers.io())

    /**
     * Get a list of ethnics
     */
    fun getEthnics(filter: QueryFilter): Observable<Page<Ethnicity>> =
        api.queryEthnics(filter).subscribeOn(Schedulers.io())

}
