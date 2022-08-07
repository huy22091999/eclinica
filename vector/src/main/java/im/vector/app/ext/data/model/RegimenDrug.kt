package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class RegimenDrug(
    @SerializedName("id")
    val drug: Drug? = null,

    @SerializedName("regimen")
    val regimen: Regimen? = null,

    @SerializedName("numberDays")
    val drugDays: Int? = null,

    @SerializedName("numberPerDay")
    val perDay: Int? = null,

    @SerializedName("note")
    val note: String? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
