package im.vector.app.ext.examination

import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.model.Prescription
import im.vector.app.ext.data.model.QueryFilter
import im.vector.app.ext.data.repository.PrescriptionRepository

class ExaminationViewModel @AssistedInject constructor(
    @Assisted initialState: ExaminationViewState,
    val repository: PrescriptionRepository
) : VectorViewModel<ExaminationViewState, ExaminationAction, ExaminationViewEvents>(initialState) {

    private val defaultFilter = QueryFilter(pageIndex = 1, pageSize = 10)

    @AssistedFactory
    interface Factory {
        fun create(initialState: ExaminationViewState): ExaminationViewModel
    }

    companion object : MvRxViewModelFactory<ExaminationViewModel, ExaminationViewState> {

        override fun initialState(viewModelContext: ViewModelContext): ExaminationViewState {
            return ExaminationViewState(
                asyncEditPrescription = Uninitialized,
                asyncPrescription = Uninitialized,
                asyncPagePrescriptions = Uninitialized,
            )
        }
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: ExaminationViewState): ExaminationViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    override fun handle(action: ExaminationAction) {
        when(action) {
            is ExaminationAction.QueryExamination -> handleQueryPrescription()
            is ExaminationAction.QueryOneById -> handleQueryOneById(action.id)
            is ExaminationAction.LaunchDetailFragment -> {
                _viewEvents.post(ExaminationViewEvents.LaunchDetailFragment)
            }
            is ExaminationAction.LaunchUpdateMethodReceive -> {
                _viewEvents.post(ExaminationViewEvents.LaunchUpdateMethodReceive)
            }
            is ExaminationAction.UpdateExamination -> handleUpdatePrescription(action.prescription)
            is ExaminationAction.UpdateStatusReceived -> handleUpdateStatusReceived(action.prescription)
        }
    }

    private fun handleQueryPrescription() {
        setState {
            copy(asyncPagePrescriptions = Loading())
        }
        repository.searchByPage(defaultFilter).execute {
            copy(asyncPagePrescriptions = it)
        }
    }

    private fun handleQueryOneById(id: String) {
        setState {
            copy(asyncPrescription = Loading())
        }
        repository.getOneById(id).execute {
            copy(asyncPrescription = it)
        }
    }

    private fun handleUpdatePrescription(prescription: Prescription) {
        setState {
            copy(asyncPrescription = Loading())
        }
        repository.updateReceive(prescription, prescription.id!!).execute {
            asyncPrescription.invoke().let {
                _viewEvents.post(ExaminationViewEvents.UpdateExaminationComplete)
            }
            copy(asyncPrescription = it)
        }
    }

    private fun handleUpdateStatusReceived(prescription: Prescription) {
        setState {
            copy(asyncPrescription = Loading())
        }
        repository.updateStatusReceived(prescription, prescription.id!!).execute {
            asyncPrescription.invoke().let {
                _viewEvents.post(ExaminationViewEvents.UpdateStatusReceivedComplete)
            }
            copy(asyncPrescription = it)
        }
    }

}
