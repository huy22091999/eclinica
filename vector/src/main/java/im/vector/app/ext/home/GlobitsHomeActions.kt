package im.vector.app.ext.home

import im.vector.app.core.platform.VectorViewModelAction

sealed class GlobitsHomeActions : VectorViewModelAction {
    object InitAction: GlobitsHomeActions()
    object GetStepMessage : GlobitsHomeActions()
}
