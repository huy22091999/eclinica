package im.vector.app.ext.profile

import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.ext.data.type.AddressType
import im.vector.app.ext.registration.RegistrationViewEvents

sealed class ProfileViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : ProfileViewEvents()
    data class Failure(val throwable: Throwable) : ProfileViewEvents()

    data class ApplyNewDistricts(val addressType: AddressType, val clearCommune: Boolean? = true) :
        ProfileViewEvents()

    data class ApplyNewCommunes(val addressType: AddressType) : ProfileViewEvents()


}
