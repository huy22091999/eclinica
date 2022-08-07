package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class PatientPeriodPrepScreen(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("patient")
    var patient: Patient? = null,

    @SerializedName("treatmentPeriod")
    var txPeriod: TreatmentPeriod? = null,

    @SerializedName("source")
    var source: Int? = null, //Nguồn biết đến  0 - tự đến, 1 - CSYT Giới thiệu, 2 -NVTCCĐ/Nhóm cộng đòngo giới thiệu, 3-khác

    @SerializedName("facilityName")
    var facilityName: String? = null, // //Tên cơ sở nếu source =1

    @SerializedName("groupName")
    var groupName: String? = null, // //Tên nhóm nếu source = 2

    @SerializedName("otherSouces")
    var otherSouces: String? = null, //Nguồn khác nếu source = 3

    @SerializedName("question1")
    var question1: Int? = null, //0/1

    @SerializedName("question2")
    var question2: Int? = null, // 0/1/2/3/4

    @SerializedName("question2Text")
    var question2Text: String? = null, // Note text

    @SerializedName("question3")
    var question3: Int? = null, // 0/1

    @SerializedName("question31")
    var question31: Int? = null, // 0/1

    @SerializedName("question32")
    var question32: Int? = null, // 0/1

    @SerializedName("question4")
    var question4: Int? = null, // 0/1

    @SerializedName("question41")
    var question41: Int? = null, // 0/1

    @SerializedName("question42")
    var question42: Int? = null, // 0/1

    @SerializedName("question5")
    var question5: Int? = null, // 0/1

    @SerializedName("question51")
    var question51: Int? = null, // 0/1/2

    @SerializedName("question52")
    var question52: Int? = null, // 0/1/2

    @SerializedName("question53")
    var question53: Int? = null, // 0/1/2

    @SerializedName("question54")
    var question54: Int? = null, // 0/1/2

    @SerializedName("question55")
    var question55: Int? = null, // 0/1/2

    @SerializedName("question6")
    var question6: Int? = null, // 0/1/2

    @SerializedName("question61")
    var question61: Int? = null, // 0/1/2

    @SerializedName("question7")
    var question7: Int? = null, // 0/1/2

    @SerializedName("question8")
    var question8: Int? = null, // 0/1/2

    @SerializedName("question9")
    var question9: Int? = null, // 0/1/2

    @SerializedName("question10")
    var question10: Int? = null, // 0/1/2

    @SerializedName("question11")
    var question11: Int? = null, // 0/1/2

    @SerializedName("question12")
    var question12: Int? = null, // 0/1/2

    @SerializedName("question13")
    var question13: Int? = null, // 0/1/2

    @SerializedName("question14")
    var question14: Int? = null, // 0/1/2

    @SerializedName("question15")
    var question15: Int? = null, // 0/1/2

    @SerializedName("question16")
    var question16: Int? = null, // 0/1/2

    @SerializedName("statusResult")
    var statusResult: Int? = null, // Kết luận 0 = Xét nghiệm chỉ đinh prep, 1 **= Danh giá prep, 2 = Theo dõi tình trạng HIC cấp

    @SerializedName("highRisk")
    var riskGroups: MutableList<Int>? = null,

    @SerializedName("highRisk1")
    var riskGroup1: Int? = null,

    @SerializedName("medicalStaffEvaluation")
    var healthStaffEval: String? = null,

    @SerializedName("situationPrep")
    var situationalPrep: Boolean? = null, // Prep tình huống

    @SerializedName("confirmationDate")
    var confirmDate: Date? = null, // Ngày xác nhận

    @SerializedName("screeningDay")
    var screeningDate: Date? = null // Ngày sàng lọc

) {
    override fun toString(): String {
        return super.toString()
    }
}
