package im.vector.app.ext.laboratory

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.Uninitialized
import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.data.model.Page

data class LaboratoryViewState(
    val asyncPageLabTests: Async<Page<LabTestOrder>> = Uninitialized,
    val asyncLabTest: Async<LabTestOrder> = Uninitialized,
) : MvRxState {

    fun isLoading(): Boolean {
        return asyncPageLabTests is Loading
                || asyncLabTest is Loading
    }
}
