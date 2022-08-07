package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class ValueCode(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("system")
    val system: String? = null,

    @SerializedName("dataType")
    val dataType: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("regimenId")
    val regimenId: String? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
