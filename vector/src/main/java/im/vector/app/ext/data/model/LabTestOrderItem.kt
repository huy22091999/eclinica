package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class  LabTestOrderItem(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("labTestCode")
    val labTestCode: String? = null,

    @SerializedName("labTestAndService")
    var labTestAndService: LabTestService? = null,

    @SerializedName("order")
    val order: LabTestOrder? = null,

    @SerializedName("labTest")
    val labTest: LabTest? = null,

    @SerializedName("affiliatedLab")
    val affiliatedLab: AffiliatedLab? = null,

    @SerializedName("encounter")
    val encounter: Encounter? = null,

    @SerializedName("orderType")
    val orderType: Int? = null,

    @SerializedName("numericResult")
    val numericResult: Double? = null,

    @SerializedName("stringResult")
    val stringResult: String? = null,

    @SerializedName("specimenDate")
    val sampleDate: Date? = null,

    @SerializedName("performedDate")
    val performedDate: Date? = null,

    @SerializedName("quantity")
    val quantity: Int? = null,

    @SerializedName("price")
    val unitPrice: Double? = null,

    @SerializedName("subTotal")
    val subTotal: Double? = null,

    @SerializedName("refuse")
    val refuse: Boolean? = null,

    @SerializedName("reasonForRefusal")
    val reasonForRefusal: String? = null,

    @SerializedName("resultsAvailable")
    val resultsAvailable: Boolean? = null,

    @SerializedName("canRefuseTest")
    val optional: Boolean? = null,

    @SerializedName("compulsoryToDoNewTest")
    val required: Boolean? = null,

    var isExpanded: Boolean? = true

) {
    override fun toString(): String {
        return super.toString()
    }
}
