package im.vector.app.ext.laboratory

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.ext.registration.RegistrationActions

sealed class LaboratoryAction : VectorViewModelAction {
    data class GetOneById(val id: String): LaboratoryAction()
    object InitAction: LaboratoryAction()
    object ViewTemplateDetail : LaboratoryAction()
    object ResetLabTestData : LaboratoryAction()

}
