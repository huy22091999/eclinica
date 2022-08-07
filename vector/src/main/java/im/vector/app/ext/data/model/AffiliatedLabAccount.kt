package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName

data class AffiliatedLabAccount(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("displayName")
    val displayName: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("email")
    val email: String? = null
) {
    override fun toString(): String {
        return super.toString()
    }
}
