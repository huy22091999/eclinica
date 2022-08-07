package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("userDto")
    val user: User? = null,

    @SerializedName("isAdmin")
    val isAdmin: Boolean? = null,

    @SerializedName("isUser")
    val isUser: Boolean? = null,

    @SerializedName("isPatient")
    val isPatient: Boolean? = null,

    @SerializedName("isPractitioner")
    val isPractitioner: Boolean? = null,

    @SerializedName("isLab")
    val isLab: Boolean? = null,

    @SerializedName("isClinic")
    val isClinic: Boolean? = null,

    @SerializedName("isDrugStore")
    val isDrugStore: Boolean? = null,

    @SerializedName("patientDto")
    val patient: Patient? = null,

    @SerializedName("practitionerDto")
    val practitioner: Practitioner? = null,

    @SerializedName("pharmacyProfileDto")
    val pharmacist: PharmacyProfile? = null,

    @SerializedName("affiliatedLabDto")
    val affiliatedLab: AffiliatedLab? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
