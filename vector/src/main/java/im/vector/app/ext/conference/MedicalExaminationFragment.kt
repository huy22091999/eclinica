package im.vector.app.ext.conference

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.mvrx.*
import com.facebook.react.bridge.UiThreadUtil
import im.vector.app.R
import im.vector.app.core.platform.VectorBaseFragment
import im.vector.app.databinding.MedicalExaminationFragmentBinding
import im.vector.app.ext.data.model.*
import im.vector.app.ext.data.type.AppConst
import im.vector.app.ext.examination.list.DrugOrderAdapter
import im.vector.app.ext.registration.list.LabTestResultAdapter
import im.vector.app.ext.utils.clickWithThrottle
import im.vector.app.ext.utils.format
import java.time.Year
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MedicalExaminationFragment @Inject constructor() :
    VectorBaseFragment<MedicalExaminationFragmentBinding>() {

    private val viewModel: ConferenceViewModel by activityViewModel()


    var toolbarTitleRes: Int? = R.string.view_medical_examination

    private lateinit var encounterId: String

    private lateinit var medicalExamination: MedicalExamination
    private lateinit var scheduler: ScheduledExecutorService

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
            viewModel.handle(ConferenceViewActions.EligibleCustomer(encounterId))
            dialog.dismiss()
        }

        btnClose.clickWithThrottle {
            dialog.dismiss()
        }
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): MedicalExaminationFragmentBinding {
        return MedicalExaminationFragmentBinding.inflate(inflater, container, false)
    }

    companion object {
        const val TAG = "_REGISTRATION_MEDICAL_EXAMINATION"
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as ConferenceActivity).updateToolbar(false,true)

    }



    private fun test(){
       scheduler  =Executors.newScheduledThreadPool(1)
        scheduler.scheduleAtFixedRate(Runnable {
            UiThreadUtil.runOnUiThread {

                viewModel.handle(ConferenceViewActions.QueryMedicalExaminationById(encounterId))
                Log.e(TAG, medicalExamination.status.toString())
                if(medicalExamination.status == 6){
                    viewModel.handle(ConferenceViewActions.ServiceRating)
                    scheduler.shutdown()
                }
            }
        },10L,10L,TimeUnit.SECONDS)
    }

    override fun onResume() {
        super.onResume()
        if (this::encounterId.isInitialized) {
            viewModel.handle(ConferenceViewActions.QueryMedicalExaminationById(encounterId))
        } else {
            viewModel.handle(ConferenceViewActions.QueryPatientInfoData)
        }
        test()


    }


    override fun onStop() {
        scheduler.shutdown()
        super.onStop()
    }

    override fun invalidate() = withState(viewModel) { state ->
        updateWithState(state)
    }

    fun updateWithState(state: ConferenceViewState) {

        if (state.asyncPatientInfo is Success) {
            if (!this::encounterId.isInitialized) {
                state.asyncPatientInfo.invoke().let {
                    if (it != null) {
                        setUpPatientInfo(it)
                        encounterId = it.encounter?.id.toString()
                        viewModel.handle(
                            ConferenceViewActions.QueryMedicalExaminationById(
                                encounterId
                            )
                        )
                    }
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

    private fun setUpPatientInfo(patient :PatientInfo ){
        views.patientName.text = patient.fullName
        views.patientAddress.text = patient.address
        views.patientInfo.text = String.format("%s, %s (%s)",
            if (patient.gender == "M") "Nam" else "Nữ",
            (Year.now().toString().toInt()- patient.dob?.format()?.substring(6)?.toInt()!!).toString()
            ,
            patient.dob.format()
        )
        views.patientNeardate.text = patient.nextEncounterDate?.format()
        views.patientRegimen.text = ""
        views.patientState.text = ""

    }

    private fun populateData() {
        with(medicalExamination) {
            views.btnUpdate.clickWithThrottle {
                viewModel.handle(ConferenceViewActions.QueryMedicalExaminationById(encounterId))

            }

            if (this.eligibleCustomer == true && this.encounterType == 0) {
                views.btnEligible.visibility = View.VISIBLE
                views.btnEligible.clickWithThrottle {
                    createDialog()
                }
            }
//            views.btnServiceRating.clickWithThrottle {
//                //TODO cần viết fragment service rating khác
//                // requireActivity().snackbar("Đang phát triển...")
//                requireActivity().snackbar("Vào kết quả thăm khám để đánh giá dịch vụ")viewModel.handle(ConferenceViewActions.ServiceRating)
//            }

            views.anamesis.text = this.anamnesis
            views.pathologicalProcess.text = this.pathologicalProcess
            views.temperature.text =
                if (this.temperature != null) String.format(this.temperature.toString() + " °C") else ""
            views.pulse.text =
                if (this.pulse != null) String.format(this.pulse.toString() + " lần/phút") else ""
            views.minimumBloodPressure.text =
                if (this.minimumBloodPressure != null) String.format(this.minimumBloodPressure.toString() + " mmHg") else ""
            views.maximumBloodPressure.text =
                if (this.maximumBloodPressure != null) String.format(this.maximumBloodPressure.toString() + " mmHg") else ""
            views.breathing.text =
                if (this.breathing != null) String.format(this.breathing.toString() + " lần/phút") else ""
            views.weight.text =
                if (this.weight != null) String.format(this.weight.toString() + " kg") else ""
            views.height.text =
                if (this.height != null) String.format(this.height.toString() + " cm") else ""

            views.motorFunction.text = if (this.motorFunction != null) AppConst.motorFunction(
                this.motorFunction,
                views.motorFunction.context
            ) else ""
            views.fullBodyTestResults.text = this.fullBodyTestResults
            views.parts.text = this.parts

            views.degreeOfAdherenceTreatment.text =
                if (this.degreeOfAdherenceTreatment != null)
                    AppConst.degreeOfAdherenceTreatment(
                        this.degreeOfAdherenceTreatment,
                        views.degreeOfAdherenceTreatment.context
                    )
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
            if (this.prescription?.receiveType != null && this.prescription.receiveType == 1) {
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



}
