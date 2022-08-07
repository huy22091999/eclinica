package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Obs(
    @SerializedName("obsType")
    val obsType: Int? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("encounter")
    val encounter: Encounter? = null,

    @SerializedName("valueCode")
    val valueCode: ValueCode? = null,

    @SerializedName("dataType")
    val dataType: Int? = null,

    @SerializedName("valueInteger")
    val valueInteger: Int? = null,

    @SerializedName("valueText")
    val valueText: String? = null,

    @SerializedName("valueDouble")
    val valueDouble: Double? = null,

    @SerializedName("valueBoolean")
    val valueBoolean: Boolean? = null,

    @SerializedName("valueUuid")
    val valueUuid: String? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
