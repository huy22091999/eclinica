package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class AdminUnit(

    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("level")
    val level: Int? = null,

    @SerializedName("parent")
    val parent: AdminUnit? = null

) {
    override fun toString(): String {
        return name ?: "-"
    }
}
