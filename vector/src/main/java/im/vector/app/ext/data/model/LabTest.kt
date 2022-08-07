package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class LabTest(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("labTestType")
    val testType: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("resultType")
    val resultType: Int? = null,

    @SerializedName("minNormalRange")
    val minRange: Double? = null,

    @SerializedName("maxNormalRange")
    val maxRange: Double? = null,

    @SerializedName("normalRange")
    val normalRange: String? = null,

    @SerializedName("unit")
    val unit: String? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("billing")
    val labTestListService: List<LabTestService>? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
