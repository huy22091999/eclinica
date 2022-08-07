package im.vector.app.ext.profile

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.ext.data.model.Patient
import im.vector.app.ext.data.type.AddressType
import im.vector.app.ext.registration.RegistrationActions

sealed class ProfileAction : VectorViewModelAction {
    data class ProvinceSelected(val id: String, val addressType: AddressType, val clearCommune: Boolean? = true) :
        ProfileAction()
    data class DistrictSelected(val id: String, val addressType: AddressType) : ProfileAction()
    data class QueryPatientData(val patientId: String) : ProfileAction()
    data class QueryPeriodPrepScreen(val screeningId: String?) : ProfileAction()

    object QueryCommonData4ProfileEdit : ProfileAction()
    object QueryPatientInfoData : ProfileAction()


}
