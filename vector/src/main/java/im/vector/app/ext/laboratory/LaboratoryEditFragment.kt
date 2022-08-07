package im.vector.app.ext.laboratory

import android.view.LayoutInflater
import android.view.ViewGroup
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.GlobitsLaboratoryEditBinding

class LaboratoryEditFragment : VectorBaseFragment<GlobitsLaboratoryEditBinding>() {

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsLaboratoryEditBinding {
        return GlobitsLaboratoryEditBinding.inflate(inflater, container, false)
    }

}
