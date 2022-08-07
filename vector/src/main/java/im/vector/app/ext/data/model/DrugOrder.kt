package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class DrugOrder(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("drug")
    val drug: Drug? = null,

    @SerializedName("type")
    val type: Int? = null,

    @SerializedName("prescription")
    val prescription: Prescription? = null,

    @SerializedName("numberDays")
    val days: Int? = null,

    @SerializedName("numberPerDay")
    val perDay: Double? = null,

    @SerializedName("note")
    val note: String? = null,

    @SerializedName("totalQuantity")
    val totalQuantity: Int? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
