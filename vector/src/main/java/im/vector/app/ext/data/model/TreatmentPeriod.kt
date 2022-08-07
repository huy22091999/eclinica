package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class TreatmentPeriod(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("patient")
    val patient: Patient? = null,

    @SerializedName("isLatestPeriod")
    val latest: Boolean? = null,

    @SerializedName("startDate")
    val startDate: Date? = null,

    @SerializedName("endDate")
    val endDate: Date? = null,

    @SerializedName("periodPrepScreening")
    var periodPrepScreen: PatientPeriodPrepScreen? = null,

    @SerializedName("encounters")
    var encounters: List<Encounter>? = null,

    @SerializedName("prepScreening")
    var prepScreens: List<PatientPrepScreen>? = null,

    @SerializedName("registerType")
    var registerType: Int? = null,

    @SerializedName("indexNumber")
    var indexNumber: Int? = null,
) {
    override fun toString(): String {
        return name.toString()
    }
}
