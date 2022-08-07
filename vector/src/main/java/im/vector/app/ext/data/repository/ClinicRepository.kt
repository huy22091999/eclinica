package im.vector.app.ext.data.repository

import im.vector.app.ext.data.model.Clinic
import im.vector.app.ext.data.network.ClinicApi
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClinicRepository @Inject constructor(val api: ClinicApi){
    fun getClinics(): Observable<List<Clinic>> =
    api.getClinics().subscribeOn(Schedulers.io())

}
