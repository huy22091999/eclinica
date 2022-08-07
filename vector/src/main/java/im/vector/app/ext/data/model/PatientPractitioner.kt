package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class PatientPractitioner(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("linkId")
    var linkId: String? = null,

    @SerializedName("practitioner")
    var practitioner: Practitioner? = null,

    @SerializedName("patient")
    var patient: Patient? = null,

    @SerializedName("linkReqUserName")
    var linkReqUserName: String? = null,

    @SerializedName("linkStatus")
    var linkStatus: Int? = null, // 0 = chưa tạo link, 1 = đã tạo link nhưng chưa accept, 2 = đã accept và kết

    @SerializedName("isDoctorInvite")
    var doctorInvitation: Boolean? = null,

    @SerializedName("isPatientInvite")
    var patientInviation: Boolean? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
