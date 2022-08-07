package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

//MedicalExaminationDto extends EncounterDto
data class MedicalExamination (

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("encounterDate")
    val encounterDate: Date? = null,

    @SerializedName("startDate")
    val startDate: Date? = null,

    @SerializedName("endDate")
    val endDate: Date? = null,

    @SerializedName("encounterType")
    val encounterType: Int? = null,

    @SerializedName("serviceType")
    val serviceType: Int? = null,

    /// lists
    @SerializedName("treatmentPeriod")
    val treatmentPeriod: TreatmentPeriod? = null,

    @SerializedName("appointment")
    val appointment: Appointment? = null,

    @SerializedName("participants")
    val participants: List<EncounterPactitioner>? = null,

    @SerializedName("symptoms")
    var symptoms: List<Obs>? = null,

    @SerializedName("diagnosis")
    var diagnosis: List<Obs>? = null,

    @SerializedName("regimens")
    var regimens: List<Obs>? = null,
    /// end lists

    @SerializedName("note")
    val note: String? = null,

    @SerializedName("motorFunction")
    val motorFunction: Int? = null, // chức năng vận động

    @SerializedName("degreeOfAdherenceTreatment")
    val degreeOfAdherenceTreatment: Int? = null, //Mức độ tuân thủ điều trị

    @SerializedName("fullBodyTestResults")
    val fullBodyTestResults: String? = null, //Kết quả kiểm tra toàn thân

    @SerializedName("parts")
    val parts: String? = null, // Các bộ phận

    @SerializedName("pathologicalProcess")
    val pathologicalProcess: String? = null, // Quá trình bệnh lý

    @SerializedName("anamnesis")
    val anamnesis: String? = null, // Tiền sử bệnh

    @SerializedName("prescription")
    val prescription: Prescription? = null,

    @SerializedName("sideEffectsOfMedications")
    val sideEffectsOfMedications: String? = null, // tác dụng phụ của thuốc

    @SerializedName("symptomsOfHIVInfection")
    val symptomsOfHIVInfection: String? = null,

    @SerializedName("pregnancyStatus")
    val pregnancyStatus: String? = null,

    @SerializedName("sexuallyTransmittedDiseaseSymptoms")
    val sexuallyTransmittedDiseaseSymptoms: String? = null,// triệu chứng bệnh lây qua đường tình dục

    @SerializedName("highRiskHIVBehaviors")
    val highRiskHIVBehaviors: String? = null,

    @SerializedName("eligibleCustomer")
    val eligibleCustomer: Boolean? = null,

    @SerializedName("patientAgree")
    val patientAgree: Boolean? = null,// khách hàng đồng thuận (chỉ cho lần khám đầu)

    @SerializedName("doctorAgree")
    val doctorAgree: Boolean? = null,//// bác sĩ đồng thuận (chỉ cho lần khám đầu)

    @SerializedName("patientAgreeDay")
    val patientAgreeDay: Date? = null,

    @SerializedName("doctorAgreeDay")
    val doctorAgreeDay: Date? = null,

    @SerializedName("testEvaluation")
    val testEvaluation: String? = null, // tư vấn và đánh giá xét nghiệm của bác sĩ

    @SerializedName("testEvaluationDate")
    val testEvaluationDate: Date? = null, // Ngày đánh giá xét nghiệm

    @SerializedName("indexNumber")
    val indexNumber: Int? = null, // lần thăm khám thứ mấy trong đợt điều trị

    @SerializedName("summaryExamination")
    val summaryExamination: String? = null, // Tổng kết lần thăm khám

    @SerializedName("isLatestEncounter")
    val isLatestEncounter: Boolean? = null,// lần thăm khám cuối cùng của đợt điều trị

    @SerializedName("hasCreateNextIndicationTesting")
    val hasCreateNextIndicationTesting: Boolean? = null,// Đã tạo chỉ định cho lần xét nghiệm tiếp theo

    @SerializedName("otherDiseases")
    val otherDiseases: String? = null,

    //MedicalExaminationDto

    @SerializedName("patientId")
    val patientId: String? = null,

    @SerializedName("practitioner")
    val practitioner: Practitioner? = null, // Người khám

    /*
	 * Khám toàn thân
	 */

    @SerializedName("temperature")
    val temperature: Double? = null, // Nhiệt độ

    @SerializedName("pulse")
    val pulse: Int? = null, // Mạch

    @SerializedName("minimumBloodPressure")
    val minimumBloodPressure: Int? = null, // Huyết áp tối thiểu

    @SerializedName("maximumBloodPressure")
    val maximumBloodPressure: Int? = null, // Huyết áp tối đa

    @SerializedName("breathing")
    val breathing: Int? = null, // Nhịp thở

    @SerializedName("weight")
    val weight : Double? = null, // Cân nặng

    @SerializedName("height")
    val height: Double? = null, // Chiều cao

    /*
     * cấp thuốc - kê đơn
     */
    @SerializedName("regimenPrEP")
    val regimenPrEP: Regimen? = null, // phác đồ PrEP

    @SerializedName("nextLabTestOrder")
    var nextLabTestOrder : LabTestOrder = LabTestOrder(),

    @SerializedName("finishEncounter")
    var finishEncounter: Boolean = false,

    @SerializedName("nextEncounterDate")
    val nextEncounterDate : Date? = null,// Ngày khám kế tiếp (dự kiến)

    @SerializedName("displayNamePatient")
    val displayNamePatient: String? = null,

    @SerializedName("labTestOrder")
    val labTestOrder: List<LabTestOrder> = emptyList(),

    @SerializedName("status")
    val status: Int? = null,
) {

}
