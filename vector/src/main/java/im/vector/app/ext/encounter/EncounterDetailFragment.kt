package im.vector.app.ext.encounter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Success
import im.vector.app.R
import im.vector.app.databinding.GlobitsEncounterDetailBinding
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.examination.list.DrugOrderAdapter
import im.vector.app.ext.registration.list.LabTestResultAdapter
import java.text.SimpleDateFormat
import javax.inject.Inject

class EncounterDetailFragment @Inject constructor() : EncounterBaseFragment<GlobitsEncounterDetailBinding>() {

    override var toolbarTitleRes: Int? = R.string.encounter_detail

    private lateinit var medicalExamination: MedicalExamination

    companion object {
        const val TAG = "_ENCOUNTER_DETAIL_FRAGMENT"
    }

//    override fun onResume() {
//        super.onResume()
//        viewModel.handle(EncounterActions.QueryPatientInfoData)
//    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsEncounterDetailBinding {
        return GlobitsEncounterDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {


    }

    override fun updateWithState(state: EncounterViewState) {
        if (state.asyncMedicalExamination is Success) {
            state.asyncMedicalExamination.invoke().let {
                medicalExamination = it
                populateData()
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private fun populateData() {
        if (!this::medicalExamination.isInitialized) {
            return
        }
        with(medicalExamination) {

            if (this.treatmentPeriod != null) {
                views.treatmentPeriodIndex.text = this.treatmentPeriod.indexNumber.toString()
            }
            if (this.labTestOrder[0].template != null
                && this.labTestOrder[0].template?.correspondingTo != null) {
                views.encounterIndex.text = this.labTestOrder[0].template?.correspondingTo
            }
            views.encounterDate.text = SimpleDateFormat("dd/MM/yyyy").format(this.encounterDate)
            views.encounterStatus.text = AppConst.encounterStatus(this.status, views.encounterStatus.context)

            views.anamesis.text = this.anamnesis
            views.pathologicalProcess.text = this.pathologicalProcess
            views.temperature.text = if (this.temperature != null) String.format(this.temperature.toString() + " °C") else ""
            views.pulse.text = if (this.pulse != null) String.format(this.pulse.toString() + " lần/phút") else ""
            views.minimumBloodPressure.text = if (this.minimumBloodPressure != null) String.format(this.minimumBloodPressure.toString() + " mmHg") else ""
            views.maximumBloodPressure.text = if (this.maximumBloodPressure != null) String.format(this.maximumBloodPressure.toString() + " mmHg") else ""
            views.breathing.text = if (this.breathing != null) String.format(this.breathing.toString() + " lần/phút") else ""
            views.weight.text = if (this.weight != null) String.format(this.weight.toString() + " kg") else ""
            views.height.text = if (this.height != null) String.format(this.height.toString() + " cm") else ""

            views.motorFunction.text = if (this.motorFunction != null) AppConst.motorFunction(this.motorFunction, views.motorFunction.context) else ""
            views.fullBodyTestResults.text = this.fullBodyTestResults
            views.parts.text = this.parts

            views.degreeOfAdherenceTreatment.text =
                if (this.degreeOfAdherenceTreatment != null)
                    AppConst.degreeOfAdherenceTreatment(this.degreeOfAdherenceTreatment, views.degreeOfAdherenceTreatment.context)
                else ""
            views.sideEffectsOfMedications.text = this.sideEffectsOfMedications
            views.symptomsOfHIVInfection.text = this.symptomsOfHIVInfection
            views.pregnancyStatus.text = this.pregnancyStatus
            views.sexuallyTransmittedDiseaseSymptoms.text = this.sexuallyTransmittedDiseaseSymptoms
            views.highRiskHIVBehaviors.text = this.highRiskHIVBehaviors
            views.otherDiseases.text = this.otherDiseases
            views.testEvaluation.text = this.testEvaluation
            views.summaryExamination.text = this.summaryExamination

            //labTestOrders
            views.labtestItemView.layoutManager = LinearLayoutManager(context)
            views.labtestItemView.setHasFixedSize(true)
            if (this.labTestOrder.isNotEmpty() && this.labTestOrder[0].items != null && this.labTestOrder[0].items!!.isNotEmpty()) {
                views.labtestItemView.adapter =
                    LabTestResultAdapter(this.labTestOrder[0].items as ArrayList<LabTestOrderItem>)
            }

            //drug by regimen
            views.drugsByRegimen.layoutManager = LinearLayoutManager(context)
            views.drugsByRegimen.setHasFixedSize(true)
            if (this.prescription != null && this.prescription.drugsByRegimen != null && this.prescription.drugsByRegimen.isNotEmpty()) {
                views.drugsByRegimen.adapter =
                    DrugOrderAdapter(this.prescription.drugsByRegimen as ArrayList<DrugOrder>)
            }

            //drugsOther
            views.drugsOther.layoutManager = LinearLayoutManager(context)
            views.drugsOther.setHasFixedSize(true)
            if (this.prescription != null && this.prescription.drugsOther != null && this.prescription.drugsOther.isNotEmpty()) {
                views.drugsOther.adapter =
                    DrugOrderAdapter(this.prescription.drugsOther as ArrayList<DrugOrder>)
            }

            //nhận thuốc
            views.receivingUnitName.text = this.prescription?.receivingUnitName ?: ""
            views.receiveType.text = if (this.prescription?.receiveType != null)
                AppConst.receiveType(this.prescription.receiveType!!, views.receiveType.context)
            else ""
//            if (this.prescription.receivingUnit==null)
//            {
//                views.layoutState.visibility=View.GONE
//                views.layoutReceive.visibility=View.GONE
//                views.layoutMothod.visibility=View.GONE
//                views.layoutMothodBuy.visibility=View.VISIBLE
//                views.receiveTypeBuy.text=getString(R.string.buy_sell)
//            }
            //nếu nhận tại nhà thì hiện thêm thông tin
            if (this.prescription?.receiveType!= null && this.prescription.receiveType == 1) {
                views.layoutReceiveTypeHome.visibility = View.VISIBLE

                views.receiveName.text = this.prescription.receiveName
                views.receiveAddress.text = this.prescription.receiveAddress
                views.receivePhoneNumber.text = this.prescription.receivePhoneNumber
            }
            if (this.prescription!=null&&this.prescription.receivingUnit ==null)
            {
                views.layoutReceive.visibility=View.GONE
                views.layoutMothod.visibility=View.GONE
                views.layoutMothodBuy.visibility=View.VISIBLE
                views.receiveTypeBuy.text=getString(R.string.buy_sell)
            }

        }
    }

    override fun resetViewModel() {

    }

    override fun updateData() {

    }
}
