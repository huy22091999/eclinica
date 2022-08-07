package im.vector.app.ext.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class User(
    @SerializedName("id")
    var id: Long? = null,

    @SerializedName("displayName")
    var displayName: String? = null,

    @SerializedName("username")
    var username: String? = null,

    @SerializedName("password")
    var password: String? = null,

    @SerializedName("oldPassword")
    var oldPassword: String? = null,

    @SerializedName("confirmPassword")
    var confirmPassword: String? = null,

    @SerializedName("isSetPassword")
    var isSetPassword: Boolean? = null,

    @SerializedName("changePass")
    var changePass: Boolean? = null,

    @SerializedName("active")
    var active: Boolean? = null,

    @SerializedName("lastName")
    var lastName: String? = null,

    @SerializedName("firstName")
    var firstName: String? = null,

    @SerializedName("dob")
    var dob: Date? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("hasPhoto")
    var hasPhoto: Boolean? = null,

) {
    override fun toString(): String {
        return super.toString()
    }
}
