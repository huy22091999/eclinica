package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class LabTestOrder(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("encounter")
    val encounter: Encounter? = null,

    @SerializedName("template")
    val template: LabTestTemplate? = null,

    @SerializedName("items")
    var items: List<LabTestOrderItem>? = null,

    @SerializedName("requestDate")
    val orderDate: Date? = null,

    @SerializedName("resultFilePath")
    val resultFilePath: String? = null,

    @SerializedName("serviceType")
    val serviceType: Int? = null,

    @SerializedName("patient")
    val patient: Patient? = null,

    @SerializedName("TreatmentPeriodId")
    val txPeriodId: String? = null,

    @SerializedName("expectedDate")
    var expectedDate: Date? = null,

    @SerializedName("testCost")
    val testCost: Long? = null,

    @SerializedName("paymentStatus")
    val paymentStatus: Int? = null, //Tình trạng thanh toán : 1 - Chưa thanh toán, 2 - Đã chuyển tiền,  3- đã thanh toán(Đã nhận được tiền)

    @SerializedName("testStatus")
    val testStatus: Int? = null,

    @SerializedName("staffInformation")
    val staffInformation: String? = null,

    @SerializedName("paymentMethods")
    val paymentMethod: Int? = null,  //Phương thức thanh toán 1. Tiền mặt, 2. chuyển khoản

    @SerializedName("lastEditedDate")
    val lastEditedDate: Date? = null,

    @SerializedName("documents")
    val attachments: ArrayList<LabTestOrderAttachment>? = null,

    @SerializedName("receivingUnit")
    var receivingUnit: HealthOrganization? = null,

    @SerializedName("indexNumberTraetmentPeriod")
    val indexNumberTreatmentPeriod: Int? = null,
) {
    override fun toString(): String {
        return super.toString()
    }
}
