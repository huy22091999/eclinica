package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Question(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("questionCode")
    val code: String? = null,

    @SerializedName("question")
    var question: String? = null,

    @SerializedName("questionType")
    var questionType: Int? = null,

    @SerializedName("parent")
    var parent: Question? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
