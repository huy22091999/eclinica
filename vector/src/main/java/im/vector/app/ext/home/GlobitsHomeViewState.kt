package im.vector.app.ext.home

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.ext.data.model.ClientStepMessage
import im.vector.app.ext.data.model.Patient
import im.vector.app.ext.data.model.PatientInfo
import im.vector.app.ext.data.model.Role

data class GlobitsHomeViewState(
    val loginPatient: Async<PatientInfo> = Uninitialized,
    val stepMessage: Async<ClientStepMessage> = Uninitialized,
) : MvRxState {
    fun isLoading(): Boolean {
        return loginPatient is Loading || stepMessage is Loading
    }
}
