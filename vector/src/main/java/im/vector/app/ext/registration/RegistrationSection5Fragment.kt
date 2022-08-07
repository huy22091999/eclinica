package im.vector.app.ext.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.withState
import im.vector.app.R
import im.vector.app.databinding.GlobitsRegSection5Binding
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.examination.list.DrugOrderAdapter
import im.vector.app.ext.registration.list.LabTestResultAdapter
import im.vector.app.ext.utils.clickWithThrottle
import im.vector.app.ext.utils.snackbar
import javax.inject.Inject

class RegistrationSection5Fragment @Inject constructor() : RegistrationBaseFragment<GlobitsRegSection5Binding>() {

    override var toolbarTitleRes: Int? = R.string.view_medical_examination

    private lateinit var encounterId: String

    private lateinit var medicalExamination: MedicalExamination

//    private lateinit var labTestOrders: LabTestOrder

    //phiếu đồng thuận
    private lateinit var dialogBuilder: AlertDialog.Builder
    private lateinit var dialog: AlertDialog

    private fun createDialog() {
        dialogBuilder = AlertDialog.Builder(requireActivity())
        val eligiblePopup: View = layoutInflater.inflate(R.layout._globits_eligible_popup, null)

        val btnAgree = eligiblePopup.findViewById<Button>(R.id.btn_agree)
        val btnClose = eligiblePopup.findViewById<Button>(R.id.btn_close)

        dialogBuilder.setView(eligiblePopup)
        dialog = dialogBuilder.create()
        dialog.show()

        btnAgree.clickWithThrottle {
            viewModel.handle(RegistrationActions.EligibleCustomer(encounterId))
            dialog.dismiss()
        }

        btnClose.clickWithThrottle {
            dialog.dismiss()
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?): GlobitsRegSection5Binding {
        return GlobitsRegSection5Binding.inflate(inflater, container, false)
    }

    companion object {
        const val TAG = "_REGISTRATION_MEDICAL_EXAMINATION"
    }

    private fun handleViewEvents(event: RegistrationViewEvents) {
        when (event) {
            is RegistrationViewEvents.EligibleCustomerComplete -> {
                withState(viewModel) {
                    when (it.asyncMedicalExamination) {
                        is Success -> requireActivity().snackbar(getString(R.string.eligible_customer_successfully_saved))
                        is Fail -> requireActivity().snackbar(getString(R.string.data_save_failure))
                        else -> Unit
                    }
                }
            }
            else -> Unit
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.observeViewEvents {
            handleViewEvents(it)
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::encounterId.isInitialized) {
            viewModel.handle(RegistrationActions.QueryMedicalExaminationById(encounterId))
        }
    }

    override fun updateWithState(state: RegistrationViewState) {
        super.updateWithState(state)

        if (state.asyncPatientInfo is Success) {
            if (!this::encounterId.isInitialized) {
                state.asyncPatientInfo.invoke()?.let {
                    encounterId = it.encounter?.id.toString()
                    viewModel.handle(RegistrationActions.QueryMedicalExaminationById(encounterId))
                }
            }
        }

        if (state.asyncMedicalExamination is Success) {
            state.asyncMedicalExamination.invoke()?.let {
                medicalExamination = it
                populateData()
            }
        }
    }

    private fun populateData() {
        with(medicalExamination) {
//            if (this.doctorAgree == true) {
                views.btnEligible.visibility = View.VISIBLE
                views.btnEligible.clickWithThrottle {
                    createDialog()
                }
//            }
            views.btnServiceRating.clickWithThrottle {
                viewModel.handle(RegistrationActions.ServiceRating)
            }

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

            //nếu nhận tại nhà thì hiện thêm thông tin
            if (this.prescription?.receiveType!= null && this.prescription.receiveType == 1) {
                views.layoutReceiveTypeHome.visibility = View.VISIBLE

                views.receiveName.text = this.prescription.receiveName
                views.receiveAddress.text = this.prescription.receiveAddress
                views.receivePhoneNumber.text = this.prescription.receivePhoneNumber
            }

        }
    }

    override fun resetViewModel() {
    }

    override fun updateData() {

    }

}
