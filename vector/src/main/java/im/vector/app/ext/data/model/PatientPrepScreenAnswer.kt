package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class PatientPrepScreenAnswer(

    @SerializedName("id")
    var id: String? = null,

    @SerializedName("screening")
    var screening: PatientPrepScreen? = null,

    @SerializedName("answer")
    var answer: QuestionAnswer? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
