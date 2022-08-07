package im.vector.app.ext.examination

import im.vector.app.core.platform.VectorViewEvents

sealed class ExaminationViewEvents : VectorViewEvents {

    object LaunchDetailFragment : ExaminationViewEvents()
    object LaunchUpdateMethodReceive : ExaminationViewEvents()
    object UpdateExaminationComplete : ExaminationViewEvents()
    object UpdateStatusReceivedComplete : ExaminationViewEvents()

}
