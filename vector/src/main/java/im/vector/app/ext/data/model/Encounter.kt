package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Encounter(
    @SerializedName("id")
    var id: String? = null,

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
    val excercieFunction: Int? = null, // chức năng vận động

    @SerializedName("degreeOfAdherenceTreatment")
    val adherence: Int? = null, //Mức độ tuân thủ điều trị

    @SerializedName("fullBodyTestResults")
    val fullbodyExam: String? = null, //Kết quả kiểm tra toàn thân

    @SerializedName("parts")
    val parts: String? = null, // Các bộ phận

    @SerializedName("pathologicalProcess")
    val pathologicalProcess: String? = null, // Quá trình bệnh lý

    @SerializedName("anamnesis")
    val anamnesis: String? = null, // Tiền sử bệnh

    @SerializedName("prescription")
    val prescription: Prescription? = null,

    @SerializedName("sideEffectsOfMedications")
    val medicationSiteEffects: String? = null, // tác dụng phụ của thuốc

    @SerializedName("symptomsOfHIVInfection")
    val hivSymptoms: String? = null,

    @SerializedName("pregnancyStatus")
    val pregnancy: String? = null,

    @SerializedName("sexuallyTransmittedDiseaseSymptoms")
    val stdSymptoms: String? = null,// triệu chứng bệnh lây qua đường tình dục

    @SerializedName("highRiskHIVBehaviors")
    val riskBehaviors: String? = null,

    @SerializedName("eligibleCustomer")
    val eligible: Boolean? = null,

    @SerializedName("patientAgree")
    val patientConcurred: Boolean? = null,// khách hàng đồng thuận (chỉ cho lần khám đầu)

    @SerializedName("doctorAgree")
    val doctorConcurred: Boolean? = null,//// bác sĩ đồng thuận (chỉ cho lần khám đầu)

    @SerializedName("patientAgreeDay")
    val patientConcurredDate: Date? = null,

    @SerializedName("doctorAgreeDay")
    val doctorConcurredDate: Date? = null,

    @SerializedName("testEvaluation")
    val testEval: String? = null, // tư vấn và đánh giá xét nghiệm của bác sĩ

    @SerializedName("testEvaluationDate")
    val testEvalDate: Date? = null, // Ngày đánh giá xét nghiệm

    @SerializedName("indexNumber")
    val indexNumber: Int? = null, // lần thăm khám thứ mấy trong đợt điều trị

    @SerializedName("summaryExamination")
    val examSummary: String? = null, // Tổng kết lần thăm khám

    @SerializedName("isLatestEncounter")
    val latest: Boolean? = null,// lần thăm khám cuối cùng của đợt điều trị

    @SerializedName("hasCreateNextIndicationTesting")
    val nextTestOrderCreated: Boolean? = null,// Đã tạo chỉ định cho lần xét nghiệm tiếp theo

    @SerializedName("otherDiseases")
    val otherDiseases: String? = null,

    @SerializedName("status")
    val status: Int? = null,

    @SerializedName("templateCorrespondingTo")
    val templateCorrespondingTo: String? = null,
) {
    override fun toString(): String {
        return super.toString()
    }
}
