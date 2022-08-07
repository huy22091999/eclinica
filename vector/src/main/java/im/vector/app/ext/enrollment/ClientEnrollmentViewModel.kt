package im.vector.app.ext.enrollment

import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.model.ClientRegistration
import im.vector.app.ext.data.model.InformationResetPassword
import im.vector.app.ext.data.repository.ClinicRepository
import im.vector.app.ext.data.repository.PatientRepository
import im.vector.app.ext.data.repository.PoliciesRepository
import im.vector.app.features.login.LoginAction
import im.vector.app.features.login.LoginViewEvents
import im.vector.app.features.login.LoginViewState

class ClientEnrollmentViewModel @AssistedInject constructor(
    @Assisted initialState: LoginViewState,
    private val patientRepos: PatientRepository,
    private  val policiesRepos: PoliciesRepository,
    private val clinicRepository: ClinicRepository,
) : VectorViewModel<LoginViewState, LoginAction, LoginViewEvents>(initialState) {

    @AssistedFactory
    interface Factory {
        fun create(initialState: LoginViewState): ClientEnrollmentViewModel
    }

    companion object : MvRxViewModelFactory<ClientEnrollmentViewModel, LoginViewState> {
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: LoginViewState): ClientEnrollmentViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }
            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    override fun handle(action: LoginAction) {
        when (action) {
            is LoginAction.CheckDuplicate4EnrollClient -> handleCheckDuplicate(action.profile)
            is LoginAction.SubmitEnrollClient -> handleSubmitEnrollClient(action.profile)
            is LoginAction.HandleshowPolicies -> handleshowPolicies()
            is LoginAction.CheckUserExist->handleCheckUserExist(action.email,action.name,action.isNumber)
            is LoginAction.SendEmailResetPassword->sendEmailResetPassword(action.email,action.name,action.isNumber)
            is LoginAction.GetClinics->handleGetClinic()
            else -> Unit
        }
    }

    private fun handleGetClinic() {
        setState {
            copy(asyncClinics=Loading())
        }
        clinicRepository.getClinics().execute {
            _viewEvents.post(LoginViewEvents.GetClinicsSuccess)
            copy(asyncClinics= it)
        }
    }

    fun resetStateResetPassword(){
        setState {
            copy(asyncCheckUserExist = Uninitialized)
        }
        setState {
            copy(asyncSendEmail = Uninitialized)
        }
    }

    private fun handleCheckUserExist(email: String?, name: String?, isNumber: String?) {
        setState {
            copy(asyncCheckUserExist = Loading())
        }
        patientRepos.checkPasswordUserExist(InformationResetPassword(name,email,isNumber)).execute {
            _viewEvents.post(LoginViewEvents.ResetPasswordCheckUserExist)
            copy(asyncCheckUserExist = it)
        }

    }
    private fun sendEmailResetPassword(email:String, name:String,isNumber:String) {
        setState {
            copy(asyncSendEmail = Loading())
        }
        patientRepos.sendEmailResetPassword(InformationResetPassword(name,email,isNumber)).execute {
            _viewEvents.post(LoginViewEvents.ResetPasswordSendEmail)
            copy(asyncSendEmail = it)
        }

    }

    /////////////////////// PRIVATE ////////////////////////////

    private fun handleCheckDuplicate(profile: ClientRegistration) {
        setState {
            copy(asyncPreRegistration = Loading())
        }

        patientRepos.checkDuplicate(profile.username, profile.phoneNumber).execute {
            copy(asyncPreRegistration = it)
        }
    }

    private fun handleSubmitEnrollClient(profile: ClientRegistration) {
        setState {
            copy(asyncRegistration = Loading())
            copy(isSubmitResgister = true)
        }

        patientRepos.registerClient(profile).execute {
            copy(asyncRegistration = it)
        }
    }

    private fun  handleshowPolicies(){
      policiesRepos.getPolicies().execute {
          copy(asyncPolicies = it)
      }
    }
    fun resetRegiter(){
        setState { copy(asyncRegistration=Uninitialized) }
        setState { copy(asyncPreRegistration=Uninitialized) }
        setState { copy(isSubmitResgister=false) }
        setState { copy(asyncPolicies=Uninitialized) }
    }
}
