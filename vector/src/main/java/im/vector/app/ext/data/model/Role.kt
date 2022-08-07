package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Role(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
