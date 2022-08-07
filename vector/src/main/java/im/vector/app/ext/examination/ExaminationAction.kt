package im.vector.app.ext.examination

import im.vector.app.core.platform.VectorViewModelAction
import im.vector.app.ext.data.model.Prescription

sealed class ExaminationAction : VectorViewModelAction {
    data class QueryOneById(val id: String): ExaminationAction()
    data class UpdateExamination(val prescription: Prescription) : ExaminationAction()
    data class UpdateStatusReceived(val prescription: Prescription) : ExaminationAction()

    object QueryExamination : ExaminationAction()
    object LaunchDetailFragment : ExaminationAction()
    object LaunchUpdateMethodReceive : ExaminationAction()
}
