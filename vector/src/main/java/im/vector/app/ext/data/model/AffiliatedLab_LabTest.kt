package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class AffiliatedLab_LabTest(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("affiliatedLab")
    val affiliatedLab: AffiliatedLab? = null,

    @SerializedName("labTest")
    val labTest: LabTest? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}

data class AffiliatedLabTestFilter(
    @SerializedName("pageIndex")
    val pageIndex: Int? = 1,

    @SerializedName("pageSize")
    val pageSize: Int? = 10,

    @SerializedName("text")
    val keyword: String? = null,

    @SerializedName("affiliatedLabId")
    val labId: String? = null
)
