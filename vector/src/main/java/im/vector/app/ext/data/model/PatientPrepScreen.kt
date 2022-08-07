package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class PatientPrepScreen(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("treatmentPeriod")
    var treatmentPeriod: TreatmentPeriod? = null,

    @SerializedName("screeningAnswers")
    var answers: List<PatientPrepScreenAnswer>? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
