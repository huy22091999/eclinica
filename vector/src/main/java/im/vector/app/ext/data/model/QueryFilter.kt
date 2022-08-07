package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class QueryFilter(
    @SerializedName("pageIndex")
    val pageIndex: Int? = 1,

    @SerializedName("pageSize")
    val pageSize: Int? = 10,

    @SerializedName("text")
    val keyword: String? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
