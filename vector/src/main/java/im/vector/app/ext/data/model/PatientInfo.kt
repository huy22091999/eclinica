package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import org.matrix.android.sdk.api.util.MatrixItem
import java.util.*

data class PatientInfo(

    @SerializedName("patientId")
    val patientId: String? = null,

    @SerializedName("patientStatus")
    val patientStatus: Int? = null,

    @SerializedName("lastTreatmentPeriodId")
    val lastTreatmentPeriodId: String? = null,

    @SerializedName("lastPatientPrepScreeningId")
    val lastPatientPrepScreeningId: String? = null,

    @SerializedName("lastPatientPrepScreeningResult")
    val lastPatientPrepScreeningResult: Int? = null,

    @SerializedName("displayName")
    val fullName: String? = null,

    @SerializedName("userName")
    val username: String? = null,

    @SerializedName("dob")
    val dob: Date? = null,

    @SerializedName("identityCardNumber")
    val identityCardNumber: String? = null,

    @SerializedName("passportNumber")
    val passportNumber: String? = null,

    @SerializedName("code")
    val code: String? = null, // Mã khách hàng

    @SerializedName("treatmentStartDate")
    val treatmentStartDate: Date? = null,

    @SerializedName("latestEncounterDate")
    val latestEncounterDate: Date? = null,

    @SerializedName("nextEncounterDate")
    val nextEncounterDate: Date? = null,

    @SerializedName("serviceType")
    val serviceType: String? = null,

    @SerializedName("height")
    val height: Double? = null,

    @SerializedName("weight")
    val weight: Double? = null,

    @SerializedName("ethnicity")
    val ethnicity: Ethnicity? = null,

    @SerializedName("gender")
    val gender: String? = null,

    @SerializedName("newUpdate")
    val infoUpdated: Boolean? = null,


    /*
	 * Trạng thái hiện thời treatmentPeriod = null => chưa đăng ký điều trị 0 = Đã
	 * khảo sát chờ Lựa chọn XN 1 = Đã lựa chọn XN 2 = Đợi cập nhật kết quả 3 = Đợi
	 * đặt lịch 4 = Đã đặt lịch chờ xác nhận 5 = Lịch đã được xác nhận 6 = Đã khám
	 * chờ tái khám -1 = Đang dừng điều trị -2 = Đã chuyển -3 = Đã bỏ trị
	 *
	 * Trạng thái điều trị của BN ( lấy từ encounterStatus (của encounter cuối cùng trong đợt điều trị) )
	 */
    @SerializedName("treatmentStatus")
    val treatmentStatus: Int? = null,

    @SerializedName("latestLabTest")
    val latestLabTest: LabTestOrder? = null,

    @SerializedName("practitioners")
    val practitioners: List<PatientPractitioner>? = null,

    @SerializedName("lastPractitioner")
    val lastPractitioner: EncounterPactitioner? = null,

    @SerializedName("encounter")
    val encounter: Encounter? = null,

    @SerializedName("registerPlace")
    val enrollmentPlace: String? = null,

    @SerializedName("currentStartDateOfTreatment")
    val currentTxStartDate: Date? = null,

    @SerializedName("listRegiment")
    val regimens: List<Regimen>? = null,

    @SerializedName("listLabTestOrderItem")
    val recentLabOrders: List<LabTestOrderItem>? = null,

    @SerializedName("listEncounter")
    val recentEncounters: List<Encounter>? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("resultCreatinine")
    val labCreatinine: String? = null,

    @SerializedName("resultAlt")
    val labALT: String? = null,

    @SerializedName("periodStatus")
    val periodStatus: Int? = null,

    @SerializedName("encounterStatus")
    val encounterStatus: Int? = null
) {
    override fun toString(): String {
        return fullName ?: "-"
    }

    fun toMatrixItem(userId: String) = MatrixItem.UserItem(userId, fullName, null)
}
