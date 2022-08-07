package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("fees")
    val fees: List<ServiceFee>? = null,

    @SerializedName("price")
    val price: Double? = null, // Biểu giá tại thời điểm hiện thời

    @SerializedName("type")
    val type: Int? = null //  Loại dịch vụ: Tại phòng khám = 1, Tự xét nghiệm = 2, , Tại đơn vị liên kết = 3, Tại nhà = 4
) {
    override fun toString(): String {
        return super.toString()
    }
}
