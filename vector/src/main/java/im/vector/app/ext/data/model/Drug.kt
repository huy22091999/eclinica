package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Drug(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("manufacturer")
    val manufacturer: String? = null,

    @SerializedName("remainingDrug")
    val remaining: Double? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}

data class DrugFilter(
    @SerializedName("pageIndex")
    val pageIndex: Int? = 1,

    @SerializedName("pageSize")
    val pageSize: Int? = 10,

    @SerializedName("text")
    val keyword: String? = null,

    @SerializedName("except")
    val excludedIds: List<String>? = null,

    @SerializedName("regimenId")
    val regimenId: String? = null
)
