package im.vector.app.ext.laboratory

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.GlobitsLaboratoryDetailBinding
import im.vector.app.ext.data.model.LabTestOrder
import im.vector.app.ext.data.model.LabTestOrderItem
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.registration.list.LabTestResultAdapter
import java.text.SimpleDateFormat
import javax.inject.Inject

class LabTestTemplateDetailFragment @Inject constructor() :
    VectorBaseFragment<GlobitsLaboratoryDetailBinding>() {

    private val viewModel: LaboratoryViewModel by activityViewModel()

    private lateinit var labTestOrder: LabTestOrder

    companion object {
        const val TAG = "_LAB_TEST_TEMPLATE_DETAIL"
    }

    private var toolbarTitleRes: Int? = R.string.labtest_detail

    override fun onResume() {
        super.onResume()

        if (toolbarTitleRes != null) {
            (activity as LaboratoryActivity).updateToolbarTitle(getString(toolbarTitleRes!!))
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsLaboratoryDetailBinding {
        return GlobitsLaboratoryDetailBinding.inflate(inflater, container, false)
    }


    override fun invalidate() = withState(viewModel) {
        updateWithState(it)
    }

    fun updateWithState(state: LaboratoryViewState) {
        when (state.asyncLabTest) {
            is Success -> {
                state.asyncLabTest.invoke().let {
                    labTestOrder = it
                    populateData()
                }
            }
            else -> Unit
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private fun populateData() {
        if (!this::labTestOrder.isInitialized) {
            return
        }

        with(labTestOrder) {
            views.treatmentPeriodIndex.text = this.indexNumberTreatmentPeriod.toString()
            views.supporter.text = this.staffInformation
            if (this.template != null) {
                views.encounterIndex.text = this.template.correspondingTo
                views.labTestTemplate.text = this.template.name
            }
            if (this.orderDate != null) {
                views.labTestRequestDate.text = SimpleDateFormat("dd/MM/yyyy").format(this.orderDate)
            }
            if (this.expectedDate != null) {
                views.labTestExpectedDate.text = SimpleDateFormat("dd/MM/yyyy").format(this.expectedDate)
            }
            if (this.encounter != null) {
                views.encounterDate.text = SimpleDateFormat("dd/MM/yyyy").format(this.encounter.encounterDate)
                views.encounterStatus.text = AppConst.encounterStatus(this.encounter.status, views.encounterStatus.context)
            }

            //labTestOrders
            views.labtestItemView.layoutManager = LinearLayoutManager(context)
            views.labtestItemView.setHasFixedSize(true)
            if (this.items != null && this.items!!.isNotEmpty()) {
                views.labtestItemView.adapter =
                    LabTestResultAdapter(this.items as ArrayList<LabTestOrderItem>)
            }
        }
    }

}
