package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class QuestionAnswer(
    @SerializedName("id")
    var id: String? = null,

    @SerializedName("question")
    var question: Question? = null,

    @SerializedName("isOnlyAnswer")
    var singleAnswer: Boolean? = null, //Đáp án duy nhất ( khi chọn đáp án này thì các đáp án khác sẽ bỏ chọn hết và ngược lại)

    @SerializedName("answerContent")
    var details: String? = null,

    @SerializedName("score")
    var score: Double? = null, //Điểm của câu trả lời

    @SerializedName("isSelected")
    var selected: Boolean? = null, // Set = true nếu được chọn (Lâm sẽ set trường này bằng True và submit ngược lên cho Thông

    @SerializedName("displayOrder")
    var displayOrder: String? = null //Thứ tự hiển thị
) {
    override fun toString(): String {
        return super.toString()
    }
}
