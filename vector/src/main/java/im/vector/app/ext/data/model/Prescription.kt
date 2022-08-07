package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Prescription(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("drugsByRegimen")
    val drugsByRegimen: List<DrugOrder>? = null,

    @SerializedName("drugsOther")
    val drugsOther: List<DrugOrder>? = null,

    @SerializedName("receivingUnitName")
    val receivingUnitName: String? = null,

    @SerializedName("receiveType")
    var receiveType: Int? = null,

    @SerializedName("receiveName")
    var receiveName: String? = null,

    @SerializedName("receiveAddress")
    var receiveAddress: String? = null,

    @SerializedName("receivePhoneNumber")
    var receivePhoneNumber: String? = null,

    @SerializedName("practitioner")
    val practitioner : Practitioner? = null,

    @SerializedName("orderDate")
    val orderDate : Date? = null,

    @SerializedName("encounter")
    val encounter : Encounter? = null,

    @SerializedName("patient")
    val patient : Patient? = null,

    @SerializedName("dispensingDate")
    val dispensingDate : Date? = null,

    @SerializedName("feedback")
    var feedback : String? = null,

    @SerializedName("returnedBox")
    val returnedBox : Double? = null,

    @SerializedName("unreturnedBox")
    val unreturnedBox : Double? = null,

    @SerializedName("receivingUnit")
    val receivingUnit : HealthOrganization? = null,

    @SerializedName("sendingUnit")
    val sendingUnit : HealthOrganization? = null,

    @SerializedName("status")
    val status : Int? = null,
)
