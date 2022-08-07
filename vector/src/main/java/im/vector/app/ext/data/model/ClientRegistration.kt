package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class ClientRegistration(
    @SerializedName("displayName")
    var fullname: String? = null,

    @SerializedName("userName")
    var username: String? = null,

    @SerializedName("password")
    var password: String? = null,

    @SerializedName("phoneNumber")
    var phoneNumber: String? = null,

    @SerializedName("gender")
    var gender: String? = null,

    @SerializedName("birthDate")
    var dob: Date? = null,

    @SerializedName("serviceType")
    var serviceType: Int? = null,

    @SerializedName("email")
    var emailAddress: String? = null

) {
    override fun toString(): String {
        return super.toString()
    }
}
