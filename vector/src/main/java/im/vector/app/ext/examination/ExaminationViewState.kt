package im.vector.app.ext.examination

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.ext.data.model.Page
import im.vector.app.ext.data.model.Prescription

data class ExaminationViewState(
    val asyncPagePrescriptions: Async<Page<Prescription>> = Uninitialized,
    val asyncPrescription: Async<Prescription> = Uninitialized,
    val asyncEditPrescription: Async<Prescription> = Uninitialized,
) : MvRxState {

    fun isLoading(): Boolean {
        return asyncPagePrescriptions is Loading
                || asyncPrescription is Loading
                || asyncEditPrescription is Loading
    }

}
