package im.vector.app.ext.profile

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.ext.data.model.*

data class ProfileViewState(
//    val asyncProfile: Async<Patient> = Uninitialized,
    var asyncPatientInfo: Async<PatientInfo> = Uninitialized,
    var asyncPatient: Async<Patient> = Uninitialized,

    var asyncEthnics: Async<Page<Ethnicity>> = Uninitialized,
    var asyncOccupations: Async<Page<Occupation>> = Uninitialized,
    var asyncProvinces: Async<List<AdminUnit>> = Uninitialized,
    var asyncHealthOrganization: Async<Page<HealthOrganization>>,
    // --> dia chi thuong tru
    var asyncDistricts: Async<List<AdminUnit>> = Uninitialized,
    var asyncCommunes: Async<List<AdminUnit>> = Uninitialized,
    // --> dia chi tam tru
    var asyncDistricts2: Async<List<AdminUnit>> = Uninitialized,
    var asyncCommunes2: Async<List<AdminUnit>> = Uninitialized,

    var editingPatient: Patient? = null,
    var asyncEditingPatient: Async<Patient>? = Uninitialized,
    var asyncPrepScreen: Async<PatientPeriodPrepScreen> = Uninitialized
) : MvRxState {

    fun isLoading(): Boolean {
        return asyncPatient is Loading
                || asyncEthnics is Loading
                || asyncOccupations is Loading
                || asyncProvinces is Loading
                || asyncHealthOrganization is Loading
                || asyncDistricts is Loading
                || asyncCommunes is Loading
                || asyncDistricts2 is Loading
                || asyncCommunes2 is Loading
                || asyncEditingPatient is Loading
                || asyncPrepScreen is Loading
    }

}
