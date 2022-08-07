package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class Religion(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
