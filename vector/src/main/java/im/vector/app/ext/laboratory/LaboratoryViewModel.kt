package im.vector.app.ext.laboratory

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.model.QueryFilter
import im.vector.app.ext.data.repository.LabTestOrderRepository
import kotlinx.coroutines.launch

class LaboratoryViewModel @AssistedInject constructor(
    @Assisted initialState: LaboratoryViewState,
    private val labRepos: LabTestOrderRepository,
) : VectorViewModel<LaboratoryViewState, LaboratoryAction, LaboratoryViewEvents>(initialState) {

    @AssistedFactory
    interface Factory {
        fun create(initialState: LaboratoryViewState): LaboratoryViewModel
    }

    companion object : MvRxViewModelFactory<LaboratoryViewModel, LaboratoryViewState> {
        override fun initialState(viewModelContext: ViewModelContext): LaboratoryViewState {
            return LaboratoryViewState(
                asyncPageLabTests = Uninitialized
            )
        }
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: LaboratoryViewState): LaboratoryViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    override fun handle(action: LaboratoryAction) {
        when(action) {
            is LaboratoryAction.InitAction -> handleQueryListLabTestOrder()

            is LaboratoryAction.ViewTemplateDetail -> {
                _viewEvents.post(LaboratoryViewEvents.LaunchLabTestTemplateDetailFragment)
            }

            is LaboratoryAction.ResetLabTestData -> {
                setState {
                    copy(
                        asyncPageLabTests = Uninitialized
                    )
                }
            }

            is LaboratoryAction.GetOneById -> handleQueryOneById(action.id)
        }
    }

//    init {
//        handleQueryListLabTestOrder()
//    }

    private fun handleQueryListLabTestOrder() = viewModelScope.launch {
        setState {
            copy(asyncPageLabTests = Loading())
        }

        labRepos.getListLabTestOrder(filter = QueryFilter(1, 10)).execute {
            copy(asyncPageLabTests = it)
        }
    }

    private fun handleQueryOneById(id: String) = viewModelScope.launch {
        setState {
            copy(asyncLabTest = Loading())
        }

        labRepos.getOneById(id).execute {
            copy(asyncLabTest = it)
        }
    }
}
