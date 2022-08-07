package im.vector.app.ext.profile

import com.airbnb.mvrx.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import im.vector.app.core.platform.VectorViewModel
import im.vector.app.ext.data.model.PatientPeriodPrepScreen
import im.vector.app.ext.data.model.QueryFilter
import im.vector.app.ext.data.repository.CommonDataRepository
import im.vector.app.ext.data.repository.PatientRepository
import im.vector.app.ext.data.type.AddressType
import im.vector.app.ext.registration.RegistrationActions
import im.vector.app.ext.registration.RegistrationSection0Fragment
import im.vector.app.ext.registration.RegistrationViewEvents
import im.vector.app.ext.registration.RegistrationViewState

class ProfileViewModel @AssistedInject constructor(
    @Assisted initialState: ProfileViewState,
    private val patientRepository: PatientRepository,
    private val commonDataRepository: CommonDataRepository,

    ) : VectorViewModel<ProfileViewState, ProfileAction, ProfileViewEvents>(initialState) {

    private val defaultFilter = QueryFilter(pageIndex = 1, pageSize = 100)

    @AssistedFactory
    interface Factory {
        fun create(initialState: ProfileViewState): ProfileViewModel
    }

    companion object : MvRxViewModelFactory<ProfileViewModel, ProfileViewState> {
        override fun initialState(viewModelContext: ViewModelContext): ProfileViewState {
            return ProfileViewState(
                asyncPatient = Uninitialized,
                asyncEthnics = Uninitialized,
                asyncOccupations = Uninitialized,
                asyncProvinces = Uninitialized,
                asyncDistricts = Uninitialized,
                asyncDistricts2 = Uninitialized,
                asyncCommunes = Uninitialized,
                asyncCommunes2 = Uninitialized,
                editingPatient = null,
                asyncHealthOrganization = Uninitialized,
                asyncEditingPatient = Uninitialized,
            )
        }
        @JvmStatic
        override fun create(viewModelContext: ViewModelContext, state: ProfileViewState): ProfileViewModel {
            val factory = when (viewModelContext) {
                is FragmentViewModelContext -> viewModelContext.fragment as? Factory
                is ActivityViewModelContext -> viewModelContext.activity as? Factory
            }

            return factory?.create(state) ?: error("You should let your activity/fragment implements Factory interface")
        }
    }

    override fun handle(action: ProfileAction) {
        when (action) {
            is ProfileAction.ProvinceSelected -> handleSelectProvince(
                action.id,
                action.addressType,
                action.clearCommune
            )
            is ProfileAction.DistrictSelected -> handleSelectDistrict(action.id, action.addressType)

            is ProfileAction.QueryCommonData4ProfileEdit -> handleQueryCommonData4ProfileEdit()

            is ProfileAction.QueryPatientInfoData -> handleQueryPatientInfoData()

            is ProfileAction.QueryPatientData -> handleQueryPatientData(action.patientId)

            is ProfileAction.QueryPeriodPrepScreen -> handleQueryPeriodPrepScreen(action.screeningId)
        }
    }

    init {
        handleQueryPatientInfoData()
    }

    private fun handleQueryPeriodPrepScreen(screeningId: String?) {
        setState {
            copy(asyncPrepScreen = Loading())
        }

        if (screeningId != null) {
            patientRepository.getPatientPeriodPrepScreening(screeningId).execute {
                copy(asyncPrepScreen = it)
            }
        } else {
            setState {
                copy(asyncPrepScreen = Success(PatientPeriodPrepScreen(id = null)))
            }
        }
    }

    private fun handleQueryPatientData(patientId: String) {
        setState {
            copy(asyncPatient = Loading())
        }
        patientRepository.getPatientById(patientId).execute {
            copy(
                asyncPatient = it,
                editingPatient = it.invoke()
            )
        }
    }

    private fun handleQueryPatientInfoData() {
        setState {
            copy(asyncPatientInfo = Loading())
        }
        patientRepository.getPatientInfo().execute {
            copy(asyncPatientInfo = it)
        }
    }

    private fun handleQueryCommonData4ProfileEdit() {
        setState {
            copy(
                asyncEthnics = Loading(),
                asyncOccupations = Loading(),
                asyncProvinces = Loading()
            )
        }

        commonDataRepository.getAdminUnits().execute {
            copy(asyncProvinces = it)
        }

        commonDataRepository.getOccupations(defaultFilter).execute {
            copy(asyncOccupations = it)
        }

        commonDataRepository.getEthnics(defaultFilter).execute {
            copy(asyncEthnics = it)
        }
    }

    private fun handleSelectDistrict(id: String, addressType: AddressType) {
        setState {
            if (addressType == AddressType.PERM) {
                copy(asyncCommunes = Loading())
            } else {
                copy(asyncCommunes2 = Loading())
            }
        }

        commonDataRepository.getAdminUnits(id).execute {
            if (addressType == AddressType.PERM) {
                asyncCommunes.invoke().let {
                    _viewEvents.post(ProfileViewEvents.ApplyNewCommunes(addressType))
                }

                copy(asyncCommunes = it)
            } else {
                asyncCommunes2.invoke().let {
                    _viewEvents.post(ProfileViewEvents.ApplyNewCommunes(addressType))
                }

                copy(asyncCommunes2 = it)
            }
        }
    }

    private fun handleSelectProvince(id: String, addressType: AddressType, clearCommune: Boolean?) {
        setState {
            if (addressType == AddressType.PERM) {
                copy(asyncDistricts = Loading(), asyncCommunes = Uninitialized)
            } else {
                copy(asyncDistricts2 = Loading(), asyncCommunes2 = Uninitialized)
            }
        }

        commonDataRepository.getAdminUnits(id).execute {
            if (addressType == AddressType.PERM) {
                asyncDistricts.invoke().let {
                    _viewEvents.post(ProfileViewEvents.ApplyNewDistricts(addressType, clearCommune))
                }

                copy(asyncDistricts = it)
            } else {
                asyncDistricts2.invoke().let {
                    _viewEvents.post(ProfileViewEvents.ApplyNewDistricts(addressType, clearCommune))
                }

                copy(asyncDistricts2 = it)
            }
        }
    }

}
