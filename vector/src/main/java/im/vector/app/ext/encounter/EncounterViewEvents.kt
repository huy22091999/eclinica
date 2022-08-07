package im.vector.app.ext.encounter

import im.vector.app.core.platform.VectorViewEvents

sealed class EncounterViewEvents : VectorViewEvents {

    object LaunchDetailFragment : EncounterViewEvents()

}
