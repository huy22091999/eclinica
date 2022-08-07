package im.vector.app.ext.home

import androidx.lifecycle.viewModelScope
import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.platform.EmptyViewEvents
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.repository.PatientRepository
import im.vector.app.ext.data.repository.UserRepository
import im.vector.app.ext.registration.RegistrationViewModel
import kotlinx.coroutines.launch

class GlobitsHomeViewModel @AssistedInject constructor(
    @Assisted initialState: GlobitsHomeViewState,
    private val patientRepos: PatientRepository,
) : VectorViewModel<GlobitsHomeViewState, GlobitsHomeActions, EmptyViewEvents>(initialState) {

    @AssistedFactory
    interface Factory {
        fun create(initialState: GlobitsHomeViewState): GlobitsHomeViewModel
    }

    companion object : MvRxViewModelFactory<GlobitsHomeViewModel, GlobitsHomeViewState> {
        override fun initialState(viewModelContext: ViewModelContext): GlobitsHomeViewState {
            return GlobitsHomeViewState(
                loginPatient = Uninitialized,
                stepMessage = Uninitialized
            )
        }

        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: GlobitsHomeViewState): GlobitsHomeViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    init {
        handleGetStepMessage();
    }

    override fun handle(action: GlobitsHomeActions) {
        when (action) {
            is GlobitsHomeActions.InitAction -> handleInitAction()
            is GlobitsHomeActions.GetStepMessage -> handleGetStepMessage()
        }
    }

    private fun handleInitAction() = viewModelScope.launch {
        setState {
            copy(loginPatient = Loading())
        }

        patientRepos.getPatientInfo().execute {
            copy(loginPatient = it)
        }
    }

    private fun handleGetStepMessage() = viewModelScope.launch {
        setState {
            copy(stepMessage = Loading())
        }

        patientRepos.getStepMessage().execute {
            copy(stepMessage = it)
        }
    }
}
