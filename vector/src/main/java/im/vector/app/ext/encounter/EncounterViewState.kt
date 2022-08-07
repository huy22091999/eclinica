package im.vector.app.ext.encounter

import androidx.paging.PagedList
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.ext.data.model.Appointment
import im.vector.app.ext.data.model.MedicalExamination
import im.vector.app.ext.data.model.PatientInfo
import im.vector.app.ext.data.model.TreatmentPeriod

data class EncounterViewState(
    val asyncAppointments: Async<PagedList<Appointment>> = Uninitialized,
    val asyncPatientInfo: Async<PatientInfo> = Uninitialized,
    val asyncListTreatmentPeriod: Async<List<TreatmentPeriod>> = Uninitialized,
    val asyncMedicalExamination: Async<MedicalExamination> = Uninitialized,
) : MvRxState {

    fun isLoading(): Boolean {
        return asyncAppointments is Loading
                || asyncPatientInfo is Loading
                || asyncListTreatmentPeriod is Loading
                || asyncMedicalExamination is Loading
    }

}
