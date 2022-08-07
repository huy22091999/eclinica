package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class LabTestService(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("labTest")
    val labTest: LabTest? = null,

    @SerializedName("service")
    val service: Service? = null,

    @SerializedName("price")
    val price: Double? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
