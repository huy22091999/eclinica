package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Appointment(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("start")
    var start: Date? = null,

    @SerializedName("end")
    var end: Date? = null,

    @SerializedName("conferenceId")
    val confId: String? = null,

    @SerializedName("statusConference")
    val confStatus: Int? = null,

    @SerializedName("appointmentType")
    val type: Int? = null,

    @SerializedName("status")
    val status: Int? = null, //0= chưa có bệnh nhân, =1 đã có bệnh nhân đặt

    @SerializedName("patient")
    var patient: Patient? = null,

    @SerializedName("slot")
    var slot: EncounterSlot? = null,

    @SerializedName("place")
    var place: Int? = null,

    @SerializedName("encounter")
    val encounter: Encounter? = null,

    @SerializedName("patientPractitioner")
    val patientPractitioner: PatientPractitioner? = null,

    @SerializedName("reason")
    val reason: String? = null,

    @SerializedName("encounterId")
    var encounterId: String? = null,

    @SerializedName("patientId")
    var patientId: String? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
