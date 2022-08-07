package im.vector.app.ext.examination

import android.view.LayoutInflater
import android.view.ViewGroup
import com.airbnb.mvrx.Success
import im.vector.app.databinding.GlobitsFragmentExaminationAddBinding
import javax.inject.Inject

class ExaminationAddFragment @Inject constructor() :
    ExaminationBaseFragment<GlobitsFragmentExaminationAddBinding>() {

    companion object {
        const val TAG = "_PRESCRIPTION_DETAIL_FRAGMENT"
    }

    override fun onResume() {
        super.onResume()
        viewModel.handle(ExaminationAction.QueryExamination)
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): GlobitsFragmentExaminationAddBinding {
        return GlobitsFragmentExaminationAddBinding.inflate(inflater, container, false)
    }

    override fun updateWithState(state: ExaminationViewState) {
        if (state.asyncPrescription is Success) {
            state.asyncPrescription.invoke().let {
                populateData()
            }
        }
    }

    private fun populateData() {

    }
}
