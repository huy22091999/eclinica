package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ServiceEvaluationCardByEncounter(

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("reviewDate")
    var reviewDate: Date? = null,

    @SerializedName("patient")
    var patient: Patient? = null,

    @SerializedName("encounter")
    var encounter: Encounter? = null,

    @SerializedName("question1")
    var question1: Int? = null,

    @SerializedName("question2")
    var question2: Int? = null,

    @SerializedName("question3")
    var question3: Int? = null,

    @SerializedName("question4")
    var question4: Int? = null,

    @SerializedName("question5")
    var question5: Int? = null,

    @SerializedName("question6")
    var question6: Int? = null,

    @SerializedName("question7")
    var question7: Int? = null,

    @SerializedName("question8")
    var question8: Int? = null,

    @SerializedName("question9")
    var question9: Int? = null,

    @SerializedName("question10")
    var question10: Int? = null,

    @SerializedName("question11")
    var question11: Int? = null,

    @SerializedName("mediumRating")
    var mediumRating: Double? = null,


) {
    override fun toString(): String {
        return super.toString()
    }
}
