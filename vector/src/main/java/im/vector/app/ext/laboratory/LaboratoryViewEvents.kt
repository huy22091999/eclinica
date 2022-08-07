package im.vector.app.ext.laboratory

import im.vector.app.core.platform.VectorViewEvents
import im.vector.app.ext.registration.RegistrationViewEvents

sealed class LaboratoryViewEvents : VectorViewEvents {
    data class Loading(val message: CharSequence? = null) : LaboratoryViewEvents()
    data class Failure(val throwable: Throwable) : LaboratoryViewEvents()

    object LaunchLabTestTemplateDetailFragment : LaboratoryViewEvents()
}
